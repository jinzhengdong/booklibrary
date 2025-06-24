package com.library.service;

import com.library.dto.request.*;
import com.library.dto.response.*;
import com.library.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 * 提供分类管理的核心业务功能
 * 
 * @author Library Management System
 * @version 1.0.0
 */
public interface CategoryService {

    /**
     * 创建分类
     * 
     * @param request 分类创建请求
     * @return 操作结果
     */
    ApiResponse<CategoryInfoResponse> createCategory(CategoryCreateRequest request);

    /**
     * 更新分类信息
     * 
     * @param categoryId 分类ID
     * @param request 分类更新请求
     * @return 操作结果
     */
    ApiResponse<CategoryInfoResponse> updateCategory(Long categoryId, CategoryUpdateRequest request);

    /**
     * 删除分类
     * 
     * @param categoryId 分类ID
     * @return 操作结果
     */
    ApiResponse<Void> deleteCategory(Long categoryId);

    /**
     * 根据ID获取分类详细信息
     * 
     * @param categoryId 分类ID
     * @return 分类详细信息
     */
    ApiResponse<CategoryInfoResponse> getCategoryById(Long categoryId);

    /**
     * 根据分类代码获取分类信息
     * 
     * @param categoryCode 分类代码
     * @return 分类信息
     */
    ApiResponse<CategoryInfoResponse> getCategoryByCode(String categoryCode);

    /**
     * 分页查询分类列表
     * 
     * @param request 查询请求
     * @return 分页分类列表
     */
    ApiResponse<CategoryListResponse> getCategoryList(CategoryQueryRequest request);

    /**
     * 获取分类简要信息列表（不分页）
     * 
     * @param isActive 是否启用（可选）
     * @param parentCategoryId 父分类ID（可选）
     * @return 分类简要信息列表
     */
    ApiResponse<List<CategorySimpleResponse>> getCategorySimpleList(Boolean isActive, Long parentCategoryId);

    /**
     * 获取顶级分类列表
     * 
     * @return 顶级分类列表
     */
    ApiResponse<List<CategorySimpleResponse>> getTopLevelCategories();

    /**
     * 根据父分类ID获取子分类列表
     * 
     * @param parentCategoryId 父分类ID
     * @return 子分类列表
     */
    ApiResponse<List<CategorySimpleResponse>> getChildCategories(Long parentCategoryId);

    /**
     * 获取分类树结构
     * 
     * @param includeInactive 是否包含禁用的分类
     * @return 分类树结构
     */
    ApiResponse<List<CategoryTreeResponse>> getCategoryTree(Boolean includeInactive);

    /**
     * 获取分类路径（从根分类到指定分类）
     * 
     * @param categoryId 分类ID
     * @return 分类路径
     */
    ApiResponse<List<CategorySimpleResponse>> getCategoryPath(Long categoryId);

    /**
     * 批量更新分类
     * 
     * @param request 批量更新请求
     * @return 操作结果
     */
    ApiResponse<Void> batchUpdateCategories(CategoryBatchUpdateRequest request);

    /**
     * 获取分类统计信息
     * 
     * @return 分类统计信息
     */
    ApiResponse<CategoryStatisticsResponse> getCategoryStatistics();

    /**
     * 检查分类代码是否可用
     * 
     * @param categoryCode 分类代码
     * @param excludeCategoryId 排除的分类ID（用于更新时检查）
     * @return 是否可用
     */
    ApiResponse<Boolean> checkCategoryCodeAvailable(String categoryCode, Long excludeCategoryId);

    /**
     * 启用分类
     * 
     * @param categoryId 分类ID
     * @return 操作结果
     */
    ApiResponse<Void> enableCategory(Long categoryId);

    /**
     * 禁用分类
     * 
     * @param categoryId 分类ID
     * @return 操作结果
     */
    ApiResponse<Void> disableCategory(Long categoryId);

    /**
     * 移动分类到新的父分类下
     * 
     * @param categoryId 分类ID
     * @param newParentCategoryId 新的父分类ID
     * @return 操作结果
     */
    ApiResponse<Void> moveCategory(Long categoryId, Long newParentCategoryId);

    /**
     * 更新分类排序
     * 
     * @param categoryId 分类ID
     * @param sortOrder 新的排序值
     * @return 操作结果
     */
    ApiResponse<Void> updateCategorySortOrder(Long categoryId, Integer sortOrder);

    /**
     * 获取有图书的分类列表
     * 
     * @return 有图书的分类列表
     */
    ApiResponse<List<CategorySimpleResponse>> getCategoriesWithBooks();

    /**
     * 根据分类层级获取分类列表
     * 
     * @param categoryLevel 分类层级
     * @return 分类列表
     */
    ApiResponse<List<CategorySimpleResponse>> getCategoriesByLevel(Integer categoryLevel);

    /**
     * 搜索分类（按名称模糊匹配）
     * 
     * @param categoryName 分类名称关键词
     * @return 匹配的分类列表
     */
    ApiResponse<List<CategorySimpleResponse>> searchCategories(String categoryName);

    /**
     * 验证分类是否存在且可用（用于其他模块调用）
     * 
     * @param categoryId 分类ID
     * @return 分类实体（内部使用）
     */
    Category validateCategory(Long categoryId);

    /**
     * 更新分类图书统计（由图书模块调用）
     * 
     * @param categoryId 分类ID
     * @param bookCount 图书数量变化
     */
    void updateCategoryBookCount(Long categoryId, Integer bookCount);

    /**
     * 获取分类及其所有子分类的ID列表
     * 
     * @param categoryId 分类ID
     * @return 分类ID列表（包括自身和所有子分类）
     */
    ApiResponse<List<Long>> getCategoryWithChildrenIds(Long categoryId);

    /**
     * 检查分类是否可以删除
     * 
     * @param categoryId 分类ID
     * @return 是否可以删除及原因
     */
    ApiResponse<Boolean> checkCategoryDeletable(Long categoryId);

    /**
     * 获取分类的最大排序值
     * 
     * @param parentCategoryId 父分类ID
     * @return 最大排序值
     */
    ApiResponse<Integer> getMaxSortOrder(Long parentCategoryId);
} 