package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 用户简化信息响应DTO
 * 用于列表显示，包含用户的基本信息
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleResponse {

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
     * 当前借阅数量
     */
    private Integer currentBorrowCount;

    /**
     * 最大借书数量
     */
    private Integer maxBorrowBooks;
} 