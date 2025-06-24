package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书信息实体类
 * 对应数据表：books
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("books")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图书ID（主键，自增）
     */
    @TableId(value = "book_id", type = IdType.AUTO)
    private Long bookId;

    /**
     * ISBN编号（唯一）
     */
    @TableField("isbn")
    private String isbn;

    /**
     * 图书标题
     */
    @TableField("title")
    private String title;

    /**
     * 作者
     */
    @TableField("author")
    private String author;

    /**
     * 出版社
     */
    @TableField("publisher")
    private String publisher;

    /**
     * 出版日期
     */
    @TableField("publication_date")
    private LocalDate publicationDate;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 语言
     */
    @TableField("language")
    private String language;

    /**
     * 页数
     */
    @TableField("pages")
    private Integer pages;

    /**
     * 价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 图书描述
     */
    @TableField("description")
    private String description;

    /**
     * 存放位置
     */
    @TableField("location")
    private String location;

    /**
     * 总册数
     */
    @TableField("total_copies")
    private Integer totalCopies;

    /**
     * 可借册数
     */
    @TableField("available_copies")
    private Integer availableCopies;

    /**
     * 已借册数
     */
    @TableField("borrowed_copies")
    private Integer borrowedCopies;

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

    // 图书语言枚举
    public static class Language {
        public static final String CHINESE = "Chinese";
        public static final String ENGLISH = "English";
        public static final String OTHER = "Other";
    }
} 