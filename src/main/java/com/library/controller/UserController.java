package com.library.controller;

import com.library.dto.request.*;
import com.library.dto.response.*;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户管理控制器
 * 提供用户相关的RESTful API接口
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:8081", "http://127.0.0.1:8081", "http://localhost", "http://127.0.0.1"}, maxAge = 3600)
public class UserController {

    private final UserService userService;

    // ========================================
    // 用户认证相关接口
    // ========================================

    /**
     * 用户登录
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("用户登录请求: {}", request.getUsername());
        ApiResponse<UserLoginResponse> response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 用户CRUD操作接口
    // ========================================

    /**
     * 创建用户
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserInfoResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("创建用户请求: {}", request.getUsername());
        ApiResponse<UserInfoResponse> response = userService.createUser(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取用户详细信息
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserById(@PathVariable Long userId) {
        log.debug("获取用户信息: {}", userId);
        ApiResponse<UserInfoResponse> response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新用户信息
     * PUT /api/users/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("更新用户信息: {}", userId);
        ApiResponse<UserInfoResponse> response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除用户
     * DELETE /api/users/{userId}
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        log.info("删除用户: {}", userId);
        ApiResponse<Void> response = userService.deleteUser(userId);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 用户查询接口
    // ========================================

    /**
     * 分页查询用户列表
     * GET /api/users?pageNum=1&pageSize=10&userType=student&status=active&keyword=张
     */
    @GetMapping
    public ResponseEntity<ApiResponse<UserListResponse>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        UserQueryRequest request = UserQueryRequest.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .userType(userType)
                .status(status)
                .keyword(keyword)
                .build();
        
        log.debug("分页查询用户列表: {}", request);
        ApiResponse<UserListResponse> response = userService.getUserList(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户简要信息列表（不分页）
     * GET /api/users/simple?userType=student&status=active
     */
    @GetMapping("/simple")
    public ResponseEntity<ApiResponse<List<UserSimpleResponse>>> getUserSimpleList(
            @RequestParam(required = false) String userType,
            @RequestParam(required = false) String status) {
        log.debug("获取用户简要列表: userType={}, status={}", userType, status);
        ApiResponse<List<UserSimpleResponse>> response = userService.getUserSimpleList(userType, status);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据用户名获取用户信息
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserByUsername(@PathVariable String username) {
        log.debug("根据用户名获取用户信息: {}", username);
        ApiResponse<UserInfoResponse> response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据学号获取用户信息
     * GET /api/users/student-id/{studentId}
     */
    @GetMapping("/student-id/{studentId}")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserByStudentId(@PathVariable String studentId) {
        log.debug("根据学号获取用户信息: {}", studentId);
        ApiResponse<UserInfoResponse> response = userService.getUserByStudentId(studentId);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据班级或部门获取用户列表
     * GET /api/users/class-department/{classDepartment}
     */
    @GetMapping("/class-department/{classDepartment}")
    public ResponseEntity<ApiResponse<List<UserSimpleResponse>>> getUsersByClassDepartment(
            @PathVariable String classDepartment) {
        log.debug("根据班级/部门获取用户列表: {}", classDepartment);
        ApiResponse<List<UserSimpleResponse>> response = userService.getUsersByClassDepartment(classDepartment);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 密码管理接口
    // ========================================

    /**
     * 修改用户密码
     * PUT /api/users/{userId}/password
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody UserPasswordChangeRequest request) {
        log.info("修改用户密码: {}", userId);
        ApiResponse<Void> response = userService.changePassword(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 重置用户密码
     * POST /api/users/{userId}/reset-password
     */
    @PostMapping("/{userId}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Long userId,
            @RequestParam String newPassword) {
        log.info("重置用户密码: {}", userId);
        ApiResponse<Void> response = userService.resetPassword(userId, newPassword);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 用户状态管理接口
    // ========================================

    /**
     * 激活用户账户
     * POST /api/users/{userId}/activate
     */
    @PostMapping("/{userId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long userId) {
        log.info("激活用户账户: {}", userId);
        ApiResponse<Void> response = userService.activateUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 冻结用户账户
     * POST /api/users/{userId}/freeze
     */
    @PostMapping("/{userId}/freeze")
    public ResponseEntity<ApiResponse<Void>> freezeUser(@PathVariable Long userId) {
        log.info("冻结用户账户: {}", userId);
        ApiResponse<Void> response = userService.freezeUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 批量更新用户状态
     * POST /api/users/batch-update-status
     */
    @PostMapping("/batch-update-status")
    public ResponseEntity<ApiResponse<Void>> batchUpdateStatus(@Valid @RequestBody UserBatchUpdateStatusRequest request) {
        log.info("批量更新用户状态: {} -> {}", request.getUserIds(), request.getStatus());
        ApiResponse<Void> response = userService.batchUpdateStatus(request);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 学生管理接口
    // ========================================

    /**
     * 获取即将毕业的学生列表
     * GET /api/users/graduating-students
     */
    @GetMapping("/graduating-students")
    public ResponseEntity<ApiResponse<List<UserSimpleResponse>>> getGraduatingStudents() {
        log.debug("获取即将毕业的学生列表");
        ApiResponse<List<UserSimpleResponse>> response = userService.getGraduatingStudents();
        return ResponseEntity.ok(response);
    }

    /**
     * 批量毕业学生
     * POST /api/users/batch-graduate
     */
    @PostMapping("/batch-graduate")
    public ResponseEntity<ApiResponse<Void>> batchGraduateStudents(@RequestBody List<Long> userIds) {
        log.info("批量毕业学生: {}", userIds);
        ApiResponse<Void> response = userService.batchGraduateStudents(userIds);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 数据验证接口
    // ========================================

    /**
     * 检查用户名是否可用
     * GET /api/users/check-username?username=test
     */
    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameAvailable(@RequestParam String username) {
        log.debug("检查用户名可用性: {}", username);
        ApiResponse<Boolean> response = userService.checkUsernameAvailable(username);
        return ResponseEntity.ok(response);
    }

    /**
     * 检查学号是否可用
     * GET /api/users/check-student-id?studentId=S2024001&excludeUserId=1
     */
    @GetMapping("/check-student-id")
    public ResponseEntity<ApiResponse<Boolean>> checkStudentIdAvailable(
            @RequestParam String studentId,
            @RequestParam(required = false) Long excludeUserId) {
        log.debug("检查学号可用性: {}, 排除用户: {}", studentId, excludeUserId);
        ApiResponse<Boolean> response = userService.checkStudentIdAvailable(studentId, excludeUserId);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 统计信息接口
    // ========================================

    /**
     * 获取用户统计信息
     * GET /api/users/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<UserStatisticsResponse>> getUserStatistics() {
        log.debug("获取用户统计信息");
        ApiResponse<UserStatisticsResponse> response = userService.getUserStatistics();
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 健康检查接口
    // ========================================

    /**
     * 用户模块健康检查
     * GET /api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        log.debug("用户模块健康检查");
        return ResponseEntity.ok(ApiResponse.success("用户模块运行正常"));
    }
} 