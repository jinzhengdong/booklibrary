package com.library.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * 分类创建请求DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
public class CategoryCreateRequest {

    /**
     * 分类代码（唯一标识）
     */
    @NotBlank(message = "分类代码不能为空")
    @Size(min = 1, max = 20, message = "分类代码长度必须在 1-20 个字符之间")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "分类代码只能包含大写字母和数字")
    private String categoryCode;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    @Size(min = 1, max = 100, message = "分类名称长度必须在 1-100 个字符之间")
    private String categoryName;

    /**
     * 父分类ID（可选，为空表示顶级分类）
     */
    private Long parentCategoryId;

    /**
     * 分类层级（自动计算，可选）
     */
    @Min(value = 1, message = "分类层级不能小于 1")
    @Max(value = 10, message = "分类层级不能超过 10")
    private Integer categoryLevel;

    /**
     * 排序顺序
     */
    @Min(value = 0, message = "排序顺序不能小于 0")
    @Max(value = 9999, message = "排序顺序不能超过 9999")
    private Integer sortOrder;

    /**
     * 分类描述
     */
    @Size(max = 500, message = "分类描述长度不能超过 500 个字符")
    private String description;

    /**
     * 是否启用（默认启用）
     */
    private Boolean isActive = true;
} 