package com.library.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 用户更新请求DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
public class UserUpdateRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 真实姓名
     */
    @Size(max = 100, message = "真实姓名长度不能超过 100 个字符")
    private String realName;

    /**
     * 用户类型（student/teacher/admin）
     */
    @Pattern(regexp = "^(student|teacher|admin)$", message = "用户类型必须为 student、teacher 或 admin")
    private String userType;

    /**
     * 学号或工号
     */
    @Size(max = 20, message = "学号/工号长度不能超过 20 个字符")
    private String studentId;

    /**
     * 班级或部门
     */
    @Size(max = 100, message = "班级/部门长度不能超过 100 个字符")
    private String classDepartment;

    /**
     * 联系电话
     */
    @Pattern(regexp = "^[1][3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String phone;

    /**
     * 电子邮箱
     */
    @Email(message = "请输入正确的邮箱地址")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    private String email;

    /**
     * 用户状态（active/frozen/graduated）
     */
    @Pattern(regexp = "^(active|frozen|graduated)$", message = "用户状态必须为 active、frozen 或 graduated")
    private String status;

    /**
     * 最大借书数量
     */
    @Min(value = 1, message = "最大借书数量不能小于 1")
    @Max(value = 50, message = "最大借书数量不能超过 50")
    private Integer maxBorrowBooks;

    /**
     * 最大借阅天数
     */
    @Min(value = 1, message = "最大借阅天数不能小于 1")
    @Max(value = 365, message = "最大借阅天数不能超过 365")
    private Integer maxBorrowDays;
} 