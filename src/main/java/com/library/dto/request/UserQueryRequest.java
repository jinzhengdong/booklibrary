package com.library.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;

/**
 * 用户查询请求DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryRequest {

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    @Builder.Default
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    @Builder.Default
    private Integer pageSize = 10;

    /**
     * 用户类型（student/teacher/admin）
     */
    @Pattern(regexp = "^(student|teacher|admin)$", message = "用户类型必须为 student、teacher 或 admin")
    private String userType;

    /**
     * 用户状态（active/frozen/graduated）
     */
    @Pattern(regexp = "^(active|frozen|graduated)$", message = "用户状态必须为 active、frozen 或 graduated")
    private String status;

    /**
     * 关键词搜索（姓名、用户名、学号）
     */
    private String keyword;

    /**
     * 班级或部门
     */
    private String classDepartment;

    /**
     * 排序字段
     */
    @Builder.Default
    private String sortField = "created_time";

    /**
     * 排序方向（asc/desc）
     */
    @Pattern(regexp = "^(asc|desc)$", message = "排序方向必须为 asc 或 desc")
    @Builder.Default
    private String sortOrder = "desc";
} 