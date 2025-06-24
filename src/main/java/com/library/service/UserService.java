package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.library.dto.request.*;
import com.library.dto.response.*;
import com.library.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * 提供用户管理的核心业务功能
 * 
 * @author Library Management System
 * @version 1.0.0
 */
public interface UserService {

    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应
     */
    ApiResponse<UserLoginResponse> login(UserLoginRequest request);

    /**
     * 创建用户
     * 
     * @param request 用户创建请求
     * @return 操作结果
     */
    ApiResponse<UserInfoResponse> createUser(UserCreateRequest request);

    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param request 用户更新请求
     * @return 操作结果
     */
    ApiResponse<UserInfoResponse> updateUser(Long userId, UserUpdateRequest request);

    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    ApiResponse<Void> deleteUser(Long userId);

    /**
     * 根据ID获取用户详细信息
     * 
     * @param userId 用户ID
     * @return 用户详细信息
     */
    ApiResponse<UserInfoResponse> getUserById(Long userId);

    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户信息
     */
    ApiResponse<UserInfoResponse> getUserByUsername(String username);

    /**
     * 根据学号获取用户信息
     * 
     * @param studentId 学号
     * @return 用户信息
     */
    ApiResponse<UserInfoResponse> getUserByStudentId(String studentId);

    /**
     * 分页查询用户列表
     * 
     * @param request 查询请求
     * @return 分页用户列表
     */
    ApiResponse<UserListResponse> getUserList(UserQueryRequest request);

    /**
     * 获取用户简要信息列表（不分页）
     * 
     * @param userType 用户类型（可选）
     * @param status 用户状态（可选）
     * @return 用户简要信息列表
     */
    ApiResponse<List<UserSimpleResponse>> getUserSimpleList(String userType, String status);

    /**
     * 修改用户密码
     * 
     * @param userId 用户ID
     * @param request 密码修改请求
     * @return 操作结果
     */
    ApiResponse<Void> changePassword(Long userId, UserPasswordChangeRequest request);

    /**
     * 重置用户密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 操作结果
     */
    ApiResponse<Void> resetPassword(Long userId, String newPassword);

    /**
     * 批量更新用户状态
     * 
     * @param request 批量更新状态请求
     * @return 操作结果
     */
    ApiResponse<Void> batchUpdateStatus(UserBatchUpdateStatusRequest request);

    /**
     * 获取用户统计信息
     * 
     * @return 用户统计信息
     */
    ApiResponse<UserStatisticsResponse> getUserStatistics();

    /**
     * 根据班级或部门获取用户列表
     * 
     * @param classDepartment 班级或部门
     * @return 用户列表
     */
    ApiResponse<List<UserSimpleResponse>> getUsersByClassDepartment(String classDepartment);

    /**
     * 检查用户名是否可用
     * 
     * @param username 用户名
     * @return 是否可用
     */
    ApiResponse<Boolean> checkUsernameAvailable(String username);

    /**
     * 检查学号是否可用
     * 
     * @param studentId 学号
     * @param excludeUserId 排除的用户ID（用于更新时检查）
     * @return 是否可用
     */
    ApiResponse<Boolean> checkStudentIdAvailable(String studentId, Long excludeUserId);

    /**
     * 激活用户账户
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    ApiResponse<Void> activateUser(Long userId);

    /**
     * 冻结用户账户
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    ApiResponse<Void> freezeUser(Long userId);

    /**
     * 获取即将毕业的学生列表
     * 
     * @return 即将毕业的学生列表
     */
    ApiResponse<List<UserSimpleResponse>> getGraduatingStudents();

    /**
     * 批量毕业学生
     * 
     * @param userIds 学生用户ID列表
     * @return 操作结果
     */
    ApiResponse<Void> batchGraduateStudents(List<Long> userIds);

    /**
     * 验证用户身份（用于其他模块调用）
     * 
     * @param userId 用户ID
     * @return 用户实体（内部使用）
     */
    User validateUser(Long userId);

    /**
     * 更新用户借阅统计（由借阅模块调用）
     * 
     * @param userId 用户ID
     * @param borrowCount 当前借阅数量
     * @param totalBorrowCount 累计借阅数量
     * @param overdueCount 逾期次数
     */
    void updateUserBorrowStatistics(Long userId, Integer borrowCount, Integer totalBorrowCount, Integer overdueCount);
} 