package com.library.dto.converter;

import com.library.dto.request.CategoryCreateRequest;
import com.library.dto.request.CategoryUpdateRequest;
import com.library.dto.response.*;
import com.library.entity.Category;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类转换工具类
 * 用于实体和DTO之间的转换
 * 
 * @author Library Management System
 * @version 1.0.0
 */
public class CategoryConverter {

    /**
     * Category实体转CategoryInfoResponse
     */
    public static CategoryInfoResponse toCategoryInfoResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryInfoResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .parentCategoryId(category.getParentCategoryId())
                .categoryLevel(category.getCategoryLevel())
                .sortOrder(category.getSortOrder())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .isActiveText(getActiveStatusText(category.getIsActive()))
                .createdTime(category.getCreatedTime())
                .updatedTime(category.getUpdatedTime())
                .hasChildren(false) // 需要额外查询设置
                .isLeaf(true) // 需要额外查询设置
                .depth(category.getCategoryLevel() != null ? category.getCategoryLevel() - 1 : 0)
                .build();
    }

    /**
     * Category实体转CategorySimpleResponse
     */
    public static CategorySimpleResponse toCategorySimpleResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategorySimpleResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .parentCategoryId(category.getParentCategoryId())
                .categoryLevel(category.getCategoryLevel())
                .sortOrder(category.getSortOrder())
                .isActive(category.getIsActive())
                .isActiveText(getActiveStatusText(category.getIsActive()))
                .createdTime(category.getCreatedTime())
                .hasChildren(false) // 需要额外查询设置
                .isLeaf(true) // 需要额外查询设置
                .build();
    }

    /**
     * Category实体转CategoryTreeResponse
     */
    public static CategoryTreeResponse toCategoryTreeResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryTreeResponse.builder()
                .categoryId(category.getCategoryId())
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .parentCategoryId(category.getParentCategoryId())
                .categoryLevel(category.getCategoryLevel())
                .sortOrder(category.getSortOrder())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .isActiveText(getActiveStatusText(category.getIsActive()))
                .createdTime(category.getCreatedTime())
                .hasChildren(false) // 需要额外查询设置
                .isLeaf(true) // 需要额外查询设置
                .depth(category.getCategoryLevel() != null ? category.getCategoryLevel() - 1 : 0)
                .expanded(false)
                .selected(false)
                .build();
    }

    /**
     * CategoryCreateRequest转Category实体
     */
    public static Category toCategory(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        return Category.builder()
                .categoryCode(request.getCategoryCode())
                .categoryName(request.getCategoryName())
                .parentCategoryId(request.getParentCategoryId())
                .categoryLevel(request.getCategoryLevel() != null ? request.getCategoryLevel() : 1)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    /**
     * 将CategoryUpdateRequest应用到Category实体
     */
    public static void updateCategory(Category category, CategoryUpdateRequest request) {
        if (category == null || request == null) {
            return;
        }
        
        category.setCategoryCode(request.getCategoryCode());
        category.setCategoryName(request.getCategoryName());
        category.setParentCategoryId(request.getParentCategoryId());
        
        if (request.getCategoryLevel() != null) {
            category.setCategoryLevel(request.getCategoryLevel());
        }
        
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        
        category.setDescription(request.getDescription());
        
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
    }

    /**
     * 批量转换为CategorySimpleResponse列表
     */
    public static List<CategorySimpleResponse> toCategorySimpleResponseList(List<Category> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return new ArrayList<>();
        }
        
        return categories.stream()
                .map(CategoryConverter::toCategorySimpleResponse)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换为CategoryInfoResponse列表
     */
    public static List<CategoryInfoResponse> toCategoryInfoResponseList(List<Category> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return new ArrayList<>();
        }
        
        return categories.stream()
                .map(CategoryConverter::toCategoryInfoResponse)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换为CategoryTreeResponse列表
     */
    public static List<CategoryTreeResponse> toCategoryTreeResponseList(List<Category> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return new ArrayList<>();
        }
        
        return categories.stream()
                .map(CategoryConverter::toCategoryTreeResponse)
                .collect(Collectors.toList());
    }

    /**
     * 构建分类树结构
     */
    public static List<CategoryTreeResponse> buildCategoryTree(List<Category> categories) {
        if (CollectionUtils.isEmpty(categories)) {
            return new ArrayList<>();
        }

        List<CategoryTreeResponse> treeList = toCategoryTreeResponseList(categories);
        List<CategoryTreeResponse> rootNodes = new ArrayList<>();

        // 创建根节点列表
        treeList.forEach(node -> {
            if (node.getParentCategoryId() == null) {
                rootNodes.add(node);
            }
        });

        // 构建父子关系
        treeList.forEach(node -> {
            if (node.getParentCategoryId() != null) {
                CategoryTreeResponse parent = findNodeById(treeList, node.getParentCategoryId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(node);
                    parent.setHasChildren(true);
                    parent.setIsLeaf(false);
                }
            }
        });

        return rootNodes;
    }

    /**
     * 根据ID查找树节点
     */
    private static CategoryTreeResponse findNodeById(List<CategoryTreeResponse> nodes, Long id) {
        return nodes.stream()
                .filter(node -> node.getCategoryId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取启用状态显示文本
     */
    public static String getActiveStatusText(Boolean isActive) {
        if (isActive == null) {
            return "未知";
        }
        return isActive ? "启用" : "禁用";
    }

    /**
     * 设置分类的统计信息
     */
    public static void setCategoryStatistics(CategoryInfoResponse response, Integer childCount, Integer bookCount) {
        if (response != null) {
            response.setChildCount(childCount != null ? childCount : 0);
            response.setBookCount(bookCount != null ? bookCount : 0);
            response.setHasChildren(childCount != null && childCount > 0);
            response.setIsLeaf(childCount == null || childCount == 0);
        }
    }

    /**
     * 设置分类的统计信息（简化版本）
     */
    public static void setCategoryStatistics(CategorySimpleResponse response, Integer childCount, Integer bookCount) {
        if (response != null) {
            response.setChildCount(childCount != null ? childCount : 0);
            response.setBookCount(bookCount != null ? bookCount : 0);
            response.setHasChildren(childCount != null && childCount > 0);
            response.setIsLeaf(childCount == null || childCount == 0);
        }
    }

    /**
     * 设置分类的统计信息（树形版本）
     */
    public static void setCategoryStatistics(CategoryTreeResponse response, Integer childCount, Integer bookCount) {
        if (response != null) {
            response.setChildCount(childCount != null ? childCount : 0);
            response.setBookCount(bookCount != null ? bookCount : 0);
            response.setHasChildren(childCount != null && childCount > 0);
            response.setIsLeaf(childCount == null || childCount == 0);
        }
    }

    /**
     * 设置父分类名称
     */
    public static void setParentCategoryName(CategoryInfoResponse response, String parentCategoryName) {
        if (response != null) {
            response.setParentCategoryName(parentCategoryName);
        }
    }

    /**
     * 设置父分类名称（简化版本）
     */
    public static void setParentCategoryName(CategorySimpleResponse response, String parentCategoryName) {
        if (response != null) {
            response.setParentCategoryName(parentCategoryName);
        }
    }
} 