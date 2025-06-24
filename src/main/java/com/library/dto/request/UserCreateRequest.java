package com.library.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 用户创建请求DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
public class UserCreateRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在 6-20 个字符之间")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 100, message = "真实姓名长度不能超过 100 个字符")
    private String realName;

    /**
     * 用户类型（student/teacher/admin）
     */
    @NotBlank(message = "用户类型不能为空")
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