package com.library.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 分类查询请求DTO
 * 支持分页和条件查询
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryQueryRequest {

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码不能小于 1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小不能小于 1")
    @Max(value = 100, message = "每页大小不能超过 100")
    private Integer pageSize = 10;

    /**
     * 分类代码（精确匹配）
     */
    private String categoryCode;

    /**
     * 分类名称（模糊匹配）
     */
    private String categoryName;

    /**
     * 父分类ID
     */
    private Long parentCategoryId;

    /**
     * 分类层级
     */
    @Min(value = 1, message = "分类层级不能小于 1")
    @Max(value = 10, message = "分类层级不能超过 10")
    private Integer categoryLevel;

    /**
     * 是否启用状态筛选
     */
    private Boolean isActive;

    /**
     * 关键词搜索（分类代码或分类名称）
     */
    private String keyword;

    /**
     * 是否查询顶级分类
     */
    private Boolean isTopLevel;

    /**
     * 是否包含子分类数量统计
     */
    private Boolean includeChildCount = false;

    /**
     * 是否包含图书数量统计
     */
    private Boolean includeBookCount = false;

    /**
     * 排序字段（category_code/category_name/category_level/sort_order/created_time）
     */
    private String sortField = "sort_order";

    /**
     * 排序方向（asc/desc）
     */
    private String sortOrder = "asc";
} 