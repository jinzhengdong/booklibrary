package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书分类实体类
 * 对应数据表：categories
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("categories")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID（主键，自增）
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    /**
     * 分类代码（唯一）
     */
    @TableField("category_code")
    private String categoryCode;

    /**
     * 分类名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 父分类ID
     */
    @TableField("parent_category_id")
    private Long parentCategoryId;

    /**
     * 分类层级
     */
    @TableField("category_level")
    private Integer categoryLevel;

    /**
     * 排序顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 分类描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

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
} 