package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类详细信息响应DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInfoResponse {

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
     * 父分类名称
     */
    private String parentCategoryName;

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
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 子分类数量
     */
    private Integer childCount;

    /**
     * 图书数量
     */
    private Integer bookCount;

    /**
     * 分类路径（从根分类到当前分类）
     */
    private List<CategorySimpleResponse> categoryPath;

    /**
     * 直接子分类列表
     */
    private List<CategorySimpleResponse> children;

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
} 