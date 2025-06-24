package com.library.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;

/**
 * 分类批量操作请求DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
public class CategoryBatchUpdateRequest {

    /**
     * 分类ID列表
     */
    @NotEmpty(message = "分类ID列表不能为空")
    @Size(min = 1, max = 100, message = "批量操作的分类数量必须在 1-100 之间")
    private List<Long> categoryIds;

    /**
     * 操作类型（enable/disable/delete/updateSort）
     */
    private String operationType;

    /**
     * 是否启用状态（用于批量启用/禁用）
     */
    private Boolean isActive;

    /**
     * 新的父分类ID（用于批量移动分类）
     */
    private Long newParentCategoryId;

    /**
     * 排序起始值（用于批量更新排序）
     */
    @Min(value = 0, message = "排序起始值不能小于 0")
    @Max(value = 9999, message = "排序起始值不能超过 9999")
    private Integer sortOrderStart;

    /**
     * 操作原因
     */
    @Size(max = 200, message = "操作原因长度不能超过 200 个字符")
    private String reason;
} 