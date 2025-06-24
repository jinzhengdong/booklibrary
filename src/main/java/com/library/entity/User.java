package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息实体类
 * 对应数据表：users
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（主键，自增）
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名（唯一）
     */
    @TableField("username")
    private String username;

    /**
     * 密码哈希值
     */
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 用户类型（student/teacher/admin）
     */
    @TableField("user_type")
    private String userType;

    /**
     * 学号或工号
     */
    @TableField("student_id")
    private String studentId;

    /**
     * 班级或部门
     */
    @TableField("class_department")
    private String classDepartment;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 电子邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 注册日期
     */
    @TableField("registration_date")
    private LocalDate registrationDate;

    /**
     * 用户状态（active/frozen/graduated）
     */
    @TableField("status")
    private String status;

    /**
     * 最大借书数量
     */
    @TableField("max_borrow_books")
    private Integer maxBorrowBooks;

    /**
     * 最大借阅天数
     */
    @TableField("max_borrow_days")
    private Integer maxBorrowDays;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    // 用户类型枚举
    public static class UserType {
        public static final String STUDENT = "student";
        public static final String TEACHER = "teacher";
        public static final String ADMIN = "admin";
    }

    // 用户状态枚举
    public static class UserStatus {
        public static final String ACTIVE = "active";
        public static final String FROZEN = "frozen";
        public static final String GRADUATED = "graduated";
    }
} 