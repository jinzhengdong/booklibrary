package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 分类简化信息响应DTO
 * 用于列表显示和树形结构展示
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySimpleResponse {

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
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 启用状态显示名称
     */
    private String isActiveText;

    /**
     * 子分类数量
     */
    private Integer childCount;

    /**
     * 图书数量
     */
    private Integer bookCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 是否有子分类
     */
    private Boolean hasChildren;

    /**
     * 是否为叶子节点
     */
    private Boolean isLeaf;
} 