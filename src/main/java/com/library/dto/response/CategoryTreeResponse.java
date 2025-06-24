package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类树形结构响应DTO
 * 用于展示完整的分类层级关系
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTreeResponse {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类代码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类ID
     */
    private Long parentCategoryId;

    /**
     * 分类层级
     */
    private Integer categoryLevel;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 启用状态显示名称
     */
    private String isActiveText;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 子分类数量
     */
    private Integer childCount;

    /**
     * 图书数量
     */
    private Integer bookCount;

    /**
     * 子分类列表（递归结构）
     */
    private List<CategoryTreeResponse> children;

    /**
     * 是否有子分类
     */
    private Boolean hasChildren;

    /**
     * 是否为叶子节点
     */
    private Boolean isLeaf;

    /**
     * 层级深度
     */
    private Integer depth;

    /**
     * 是否展开（前端显示用）
     */
    private Boolean expanded = false;

    /**
     * 是否选中（前端显示用）
     */
    private Boolean selected = false;
} 