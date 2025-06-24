package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.converter.UserConverter;
import com.library.dto.request.*;
import com.library.dto.response.*;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 用户服务实现类
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public ApiResponse<UserLoginResponse> login(UserLoginRequest request) {
        try {
            log.info("用户登录尝试: {}", request.getUsername());
            
            // 验证输入参数
            if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
                return ApiResponse.badRequest("用户名和密码不能为空");
            }

            // 对密码进行MD5加密
            String passwordHash = DigestUtils.md5DigestAsHex(request.getPassword().getBytes());
            
            // 验证用户登录
            User user = userMapper.validateLogin(request.getUsername(), passwordHash);
            if (user == null) {
                log.warn("用户登录失败: 用户名或密码错误 - {}", request.getUsername());
                return ApiResponse.badRequest("用户名或密码错误");
            }

            // 检查用户状态
            if (!User.UserStatus.ACTIVE.equals(user.getStatus())) {
                log.warn("用户登录失败: 账户状态异常 - {} ({})", request.getUsername(), user.getStatus());
                return ApiResponse.badRequest("账户状态异常，请联系管理员");
            }

            // 生成登录令牌（简单实现）
            String token = generateToken(user.getUserId());
            
            // 更新最后登录时间
            userMapper.updateLastLoginTime(user.getUserId());

            // 转换为响应对象
            UserLoginResponse response = UserConverter.toUserLoginResponse(user, token);
            
            log.info("用户登录成功: {} ({})", user.getUsername(), user.getRealName());
            return ApiResponse.success("登录成功", response);
            
        } catch (Exception e) {
            log.error("用户登录异常: {}", request.getUsername(), e);
            return ApiResponse.serverError("登录失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public ApiResponse<UserInfoResponse> createUser(UserCreateRequest request) {
        try {
            log.info("创建用户: {}", request.getUsername());
            
            // 验证密码一致性
            if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
                return ApiResponse.badRequest("密码和确认密码不一致");
            }

            // 检查用户名是否已存在
            User existingUser = userMapper.findByUsername(request.getUsername());
            if (existingUser != null) {
                return ApiResponse.badRequest("用户名已存在");
            }

            // 检查学号是否已存在（如果提供了学号）
            if (StringUtils.hasText(request.getStudentId())) {
                User existingStudentId = userMapper.findByStudentId(request.getStudentId());
                if (existingStudentId != null) {
                    return ApiResponse.badRequest("学号已存在");
                }
            }

            // 转换为实体对象
            User user = UserConverter.toUser(request);
            user.setRegistrationDate(LocalDate.now());
            user.setCreatedTime(LocalDateTime.now());
            user.setUpdatedTime(LocalDateTime.now());

            // 保存用户
            int result = userMapper.insert(user);
            if (result <= 0) {
                return ApiResponse.serverError("创建用户失败");
            }

            // 直接使用插入的用户对象转换响应，避免时间戳解析问题
            UserInfoResponse response = UserConverter.toUserInfoResponse(user);
            
            log.info("用户创建成功: {} ({})", user.getUsername(), user.getRealName());
            return ApiResponse.success("用户创建成功", response);
            
        } catch (Exception e) {
            log.error("创建用户异常: {}", request.getUsername(), e);
            return ApiResponse.serverError("创建用户失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public ApiResponse<UserInfoResponse> updateUser(Long userId, UserUpdateRequest request) {
        try {
            log.info("更新用户信息: {}", userId);
            
            // 查找用户
            User existingUser = userMapper.selectById(userId);
            if (existingUser == null) {
                return ApiResponse.notFound("用户不存在");
            }

            // 检查学号是否已被其他用户使用
            if (StringUtils.hasText(request.getStudentId()) && 
                !Objects.equals(request.getStudentId(), existingUser.getStudentId())) {
                User existingStudentId = userMapper.findByStudentId(request.getStudentId());
                if (existingStudentId != null && !Objects.equals(existingStudentId.getUserId(), userId)) {
                    return ApiResponse.badRequest("学号已被其他用户使用");
                }
            }

            // 应用更新
            UserConverter.updateUser(existingUser, request);
            existingUser.setUpdatedTime(LocalDateTime.now());

            // 保存更新
            int result = userMapper.updateById(existingUser);
            if (result <= 0) {
                return ApiResponse.serverError("更新用户失败");
            }

            // 查询更新后的用户信息
            User updatedUser = userMapper.selectById(userId);
            UserInfoResponse response = UserConverter.toUserInfoResponse(updatedUser);
            
            log.info("用户信息更新成功: {} ({})", updatedUser.getUsername(), updatedUser.getRealName());
            return ApiResponse.success("用户信息更新成功", response);
            
        } catch (Exception e) {
            log.error("更新用户信息异常: {}", userId, e);
            return ApiResponse.serverError("更新用户信息失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteUser(Long userId) {
        try {
            log.info("删除用户: {}", userId);
            
            // 查找用户
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }

            // 检查是否可以删除（例如：管理员账户不能删除）
            if (User.UserType.ADMIN.equals(user.getUserType())) {
                return ApiResponse.badRequest("管理员账户不能删除");
            }

            // 逻辑删除：将状态设置为graduated
            user.setStatus(User.UserStatus.GRADUATED);
            user.setUpdatedTime(LocalDateTime.now());
            
            int result = userMapper.updateById(user);
            if (result <= 0) {
                return ApiResponse.serverError("删除用户失败");
            }
            
            log.info("用户删除成功: {} ({})", user.getUsername(), user.getRealName());
            return ApiResponse.success("用户删除成功", null);
            
        } catch (Exception e) {
            log.error("删除用户异常: {}", userId, e);
            return ApiResponse.serverError("删除用户失败，请稍后重试");
        }
    }

    @Override
    public ApiResponse<UserInfoResponse> getUserById(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null || User.UserStatus.GRADUATED.equals(user.getStatus())) {
                return ApiResponse.notFound("用户不存在");
            }

            UserInfoResponse response = UserConverter.toUserInfoResponse(user);
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("查询用户信息异常: {}", userId, e);
            return ApiResponse.serverError("查询用户信息失败");
        }
    }

    @Override
    public ApiResponse<UserInfoResponse> getUserByUsername(String username) {
        try {
            User user = userMapper.findByUsername(username);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }

            UserInfoResponse response = UserConverter.toUserInfoResponse(user);
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("根据用户名查询用户信息异常: {}", username, e);
            return ApiResponse.serverError("查询用户信息失败");
        }
    }

    @Override
    public ApiResponse<UserInfoResponse> getUserByStudentId(String studentId) {
        try {
            User user = userMapper.findByStudentId(studentId);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }

            UserInfoResponse response = UserConverter.toUserInfoResponse(user);
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("根据学号查询用户信息异常: {}", studentId, e);
            return ApiResponse.serverError("查询用户信息失败");
        }
    }

    @Override
    public ApiResponse<UserListResponse> getUserList(UserQueryRequest request) {
        try {
            log.info("分页查询用户列表: {}", request);
            
            // 创建分页对象
            Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
            
            // 执行分页查询
            IPage<User> userPage = userMapper.findUsersWithConditions(
                page, 
                request.getUserType(), 
                request.getStatus(), 
                request.getKeyword()
            );

            // 转换响应对象
            List<UserSimpleResponse> userList = UserConverter.toUserSimpleResponseList(userPage.getRecords());
            
            UserListResponse response = UserListResponse.builder()
                    .users(userList)
                    .total(userPage.getTotal())
                    .pageNum(request.getPageNum())
                    .pageSize(request.getPageSize())
                    .totalPages((int) userPage.getPages())
                    .hasPrevious(userPage.getCurrent() > 1)
                    .hasNext(userPage.getCurrent() < userPage.getPages())
                    .isFirst(userPage.getCurrent() == 1)
                    .isLast(userPage.getCurrent() == userPage.getPages())
                    .build();

            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("分页查询用户列表异常", e);
            return ApiResponse.serverError("查询用户列表失败");
        }
    }

    @Override
    public ApiResponse<List<UserSimpleResponse>> getUserSimpleList(String userType, String status) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            
            if (StringUtils.hasText(userType)) {
                wrapper.eq(User::getUserType, userType);
            }
            if (StringUtils.hasText(status)) {
                wrapper.eq(User::getStatus, status);
            } else {
                wrapper.ne(User::getStatus, User.UserStatus.GRADUATED);
            }
            
            wrapper.orderByDesc(User::getCreatedTime);
            
            List<User> users = userMapper.selectList(wrapper);
            List<UserSimpleResponse> response = UserConverter.toUserSimpleResponseList(users);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("查询用户简要列表异常", e);
            return ApiResponse.serverError("查询用户列表失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> changePassword(Long userId, UserPasswordChangeRequest request) {
        try {
            log.info("修改用户密码: {}", userId);
            
            // 查找用户
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }

            // 验证原密码
            String oldPasswordHash = DigestUtils.md5DigestAsHex(request.getOldPassword().getBytes());
            if (!Objects.equals(oldPasswordHash, user.getPasswordHash())) {
                return ApiResponse.badRequest("原密码错误");
            }

            // 验证新密码一致性
            if (!Objects.equals(request.getNewPassword(), request.getConfirmNewPassword())) {
                return ApiResponse.badRequest("新密码和确认密码不一致");
            }

            // 更新密码
            String newPasswordHash = DigestUtils.md5DigestAsHex(request.getNewPassword().getBytes());
            user.setPasswordHash(newPasswordHash);
            user.setUpdatedTime(LocalDateTime.now());

            int result = userMapper.updateById(user);
            if (result <= 0) {
                return ApiResponse.serverError("密码修改失败");
            }
            
            log.info("用户密码修改成功: {}", user.getUsername());
            return ApiResponse.success("密码修改成功", null);
            
        } catch (Exception e) {
            log.error("修改用户密码异常: {}", userId, e);
            return ApiResponse.serverError("修改密码失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> resetPassword(Long userId, String newPassword) {
        try {
            log.info("重置用户密码: {}", userId);
            
            // 查找用户
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResponse.notFound("用户不存在");
            }

            // 生成新密码哈希
            String newPasswordHash = DigestUtils.md5DigestAsHex(newPassword.getBytes());
            
            // 更新密码
            user.setPasswordHash(newPasswordHash);
            user.setUpdatedTime(LocalDateTime.now());

            int result = userMapper.updateById(user);
            if (result <= 0) {
                return ApiResponse.serverError("密码重置失败");
            }
            
            log.info("用户密码重置成功: {}", user.getUsername());
            return ApiResponse.success("密码重置成功", null);
            
        } catch (Exception e) {
            log.error("重置用户密码异常: {}", userId, e);
            return ApiResponse.serverError("重置密码失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> batchUpdateStatus(UserBatchUpdateStatusRequest request) {
        try {
            log.info("批量更新用户状态: {} -> {}", request.getUserIds(), request.getStatus());
            
            if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
                return ApiResponse.badRequest("用户ID列表不能为空");
            }

            int result = userMapper.batchUpdateStatus(request.getUserIds(), request.getStatus());
            if (result <= 0) {
                return ApiResponse.serverError("批量更新状态失败");
            }
            
            log.info("批量更新用户状态成功: 影响 {} 条记录", result);
            return ApiResponse.success("批量更新状态成功", null);
            
        } catch (Exception e) {
            log.error("批量更新用户状态异常", e);
            return ApiResponse.serverError("批量更新状态失败，请稍后重试");
        }
    }

    @Override
    public ApiResponse<UserStatisticsResponse> getUserStatistics() {
        try {
            // 统计各类型用户数量
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.ne(User::getStatus, User.UserStatus.GRADUATED);
            
            Long totalUsers = userMapper.selectCount(wrapper);
            
            wrapper.clear();
            wrapper.eq(User::getUserType, User.UserType.STUDENT)
                   .eq(User::getStatus, User.UserStatus.ACTIVE);
            Long activeStudents = userMapper.selectCount(wrapper);
            
            wrapper.clear();
            wrapper.eq(User::getUserType, User.UserType.TEACHER)
                   .eq(User::getStatus, User.UserStatus.ACTIVE);
            Long activeTeachers = userMapper.selectCount(wrapper);
            
            wrapper.clear();
            wrapper.eq(User::getUserType, User.UserType.ADMIN)
                   .eq(User::getStatus, User.UserStatus.ACTIVE);
            Long activeAdmins = userMapper.selectCount(wrapper);
            
            wrapper.clear();
            wrapper.eq(User::getStatus, User.UserStatus.FROZEN);
            Long frozenUsers = userMapper.selectCount(wrapper);

            UserStatisticsResponse response = UserStatisticsResponse.builder()
                    .totalUsers(totalUsers)
                    .activeUsers(activeStudents + activeTeachers + activeAdmins)
                    .studentCount(activeStudents)
                    .teacherCount(activeTeachers)
                    .adminCount(activeAdmins)
                    .frozenUsers(frozenUsers)
                    .graduatedUsers(0L) // 如需要可以另外查询
                    .build();

            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("获取用户统计信息异常", e);
            return ApiResponse.serverError("获取统计信息失败");
        }
    }

    @Override
    public ApiResponse<List<UserSimpleResponse>> getUsersByClassDepartment(String classDepartment) {
        try {
            List<User> users = userMapper.findByClassDepartment(classDepartment);
            List<UserSimpleResponse> response = UserConverter.toUserSimpleResponseList(users);
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("根据班级/部门查询用户异常: {}", classDepartment, e);
            return ApiResponse.serverError("查询用户列表失败");
        }
    }

    @Override
    public ApiResponse<Boolean> checkUsernameAvailable(String username) {
        try {
            User user = userMapper.findByUsername(username);
            boolean available = (user == null);
            return ApiResponse.success(available);
            
        } catch (Exception e) {
            log.error("检查用户名可用性异常: {}", username, e);
            return ApiResponse.serverError("检查用户名失败");
        }
    }

    @Override
    public ApiResponse<Boolean> checkStudentIdAvailable(String studentId, Long excludeUserId) {
        try {
            User user = userMapper.findByStudentId(studentId);
            boolean available = (user == null || (excludeUserId != null && Objects.equals(user.getUserId(), excludeUserId)));
            return ApiResponse.success(available);
            
        } catch (Exception e) {
            log.error("检查学号可用性异常: {}", studentId, e);
            return ApiResponse.serverError("检查学号失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> activateUser(Long userId) {
        try {
            return updateUserStatus(userId, User.UserStatus.ACTIVE, "激活");
        } catch (Exception e) {
            log.error("激活用户异常: {}", userId, e);
            return ApiResponse.serverError("激活用户失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> freezeUser(Long userId) {
        try {
            return updateUserStatus(userId, User.UserStatus.FROZEN, "冻结");
        } catch (Exception e) {
            log.error("冻结用户异常: {}", userId, e);
            return ApiResponse.serverError("冻结用户失败");
        }
    }

    @Override
    public ApiResponse<List<UserSimpleResponse>> getGraduatingStudents() {
        try {
            List<User> students = userMapper.findGraduatingStudents();
            List<UserSimpleResponse> response = UserConverter.toUserSimpleResponseList(students);
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("查询即将毕业学生异常", e);
            return ApiResponse.serverError("查询即将毕业学生失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> batchGraduateStudents(List<Long> userIds) {
        try {
            log.info("批量毕业学生: {}", userIds);
            
            if (userIds == null || userIds.isEmpty()) {
                return ApiResponse.badRequest("学生ID列表不能为空");
            }

            int result = userMapper.batchUpdateStatus(userIds, User.UserStatus.GRADUATED);
            if (result <= 0) {
                return ApiResponse.serverError("批量毕业学生失败");
            }
            
            log.info("批量毕业学生成功: 影响 {} 条记录", result);
            return ApiResponse.success("批量毕业学生成功", null);
            
        } catch (Exception e) {
            log.error("批量毕业学生异常", e);
            return ApiResponse.serverError("批量毕业学生失败，请稍后重试");
        }
    }

    @Override
    public User validateUser(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user != null && User.UserStatus.ACTIVE.equals(user.getStatus())) {
                return user;
            }
            return null;
            
        } catch (Exception e) {
            log.error("验证用户身份异常: {}", userId, e);
            return null;
        }
    }

    @Override
    @Transactional
    public void updateUserBorrowStatistics(Long userId, Integer borrowCount, Integer totalBorrowCount, Integer overdueCount) {
        try {
            log.debug("更新用户借阅统计: userId={}, borrowCount={}, totalBorrowCount={}, overdueCount={}", 
                userId, borrowCount, totalBorrowCount, overdueCount);
            
            LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(User::getUserId, userId)
                   .set(User::getUpdatedTime, LocalDateTime.now());
            
            // 注意：这里的字段名需要根据实际的User实体类字段进行调整
            // 如果User实体中没有这些统计字段，可能需要添加或者在其他地方维护这些统计信息
            
            userMapper.update(null, wrapper);
            
        } catch (Exception e) {
            log.error("更新用户借阅统计异常: userId={}", userId, e);
        }
    }

    /**
     * 生成登录令牌
     */
    private String generateToken(Long userId) {
        return UUID.randomUUID().toString().replace("-", "") + "_" + userId + "_" + System.currentTimeMillis();
    }

    /**
     * 更新用户状态的通用方法
     */
    private ApiResponse<Void> updateUserStatus(Long userId, String status, String operation) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return ApiResponse.notFound("用户不存在");
        }

        user.setStatus(status);
        user.setUpdatedTime(LocalDateTime.now());

        int result = userMapper.updateById(user);
        if (result <= 0) {
            return ApiResponse.serverError(operation + "用户失败");
        }

        log.info("用户{}成功: {} ({})", operation, user.getUsername(), user.getRealName());
        return ApiResponse.success(operation + "用户成功", null);
    }
} 