package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息响应DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

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
     * 用户类型显示名称
     */
    private String userTypeName;

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
     * 用户状态（active/frozen/graduated）
     */
    private String status;

    /**
     * 用户状态显示名称
     */
    private String statusName;

    /**
     * 最大借书数量
     */
    private Integer maxBorrowBooks;

    /**
     * 最大借阅天数
     */
    private Integer maxBorrowDays;

    /**
     * 当前借阅数量
     */
    private Integer currentBorrowCount;

    /**
     * 累计借阅数量
     */
    private Integer totalBorrowCount;

    /**
     * 逾期次数
     */
    private Integer overdueCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
} 