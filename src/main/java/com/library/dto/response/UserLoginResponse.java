package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户登录响应DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户类型（student/teacher/admin）
     */
    private String userType;

    /**
     * 学号或工号
     */
    private String studentId;

    /**
     * 班级或部门
     */
    private String classDepartment;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 注册日期
     */
    private LocalDate registrationDate;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 最大借书数量
     */
    private Integer maxBorrowBooks;

    /**
     * 最大借阅天数
     */
    private Integer maxBorrowDays;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 访问令牌（简单实现，实际项目中可使用JWT）
     */
    private String token;

    /**
     * 令牌过期时间
     */
    private LocalDateTime tokenExpireTime;
} 