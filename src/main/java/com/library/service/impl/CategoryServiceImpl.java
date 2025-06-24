package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.dto.converter.CategoryConverter;
import com.library.dto.request.*;
import com.library.dto.response.*;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import com.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public ApiResponse<CategoryInfoResponse> createCategory(CategoryCreateRequest request) {
        try {
            log.info("创建分类: {}", request.getCategoryCode());
            
            // 检查分类代码是否已存在
            Category existingCategory = categoryMapper.findByCategoryCode(request.getCategoryCode());
            if (existingCategory != null) {
                return ApiResponse.badRequest("分类代码已存在");
            }

            // 验证父分类是否存在（如果指定了父分类）
            if (request.getParentCategoryId() != null) {
                Category parentCategory = categoryMapper.selectById(request.getParentCategoryId());
                if (parentCategory == null) {
                    return ApiResponse.badRequest("父分类不存在");
                }
                if (!parentCategory.getIsActive()) {
                    return ApiResponse.badRequest("父分类已禁用，无法添加子分类");
                }
                
                // 自动计算分类层级
                if (request.getCategoryLevel() == null) {
                    request.setCategoryLevel(parentCategory.getCategoryLevel() + 1);
                }
            } else {
                // 顶级分类层级为1
                if (request.getCategoryLevel() == null) {
                    request.setCategoryLevel(1);
                }
            }

            // 如果没有指定排序，获取同级分类的最大排序值+1
            if (request.getSortOrder() == null) {
                Integer maxSortOrder = categoryMapper.findMaxSortOrder(request.getParentCategoryId());
                request.setSortOrder(maxSortOrder + 1);
            }

            // 转换为实体对象
            Category category = CategoryConverter.toCategory(request);
            category.setCreatedTime(LocalDateTime.now());
            category.setUpdatedTime(LocalDateTime.now());

            // 保存分类
            int result = categoryMapper.insert(category);
            if (result <= 0) {
                return ApiResponse.serverError("创建分类失败");
            }

            // 转换为响应对象
            CategoryInfoResponse response = CategoryConverter.toCategoryInfoResponse(category);
            
            // 设置父分类名称
            if (category.getParentCategoryId() != null) {
                Category parentCategory = categoryMapper.selectById(category.getParentCategoryId());
                if (parentCategory != null) {
                    CategoryConverter.setParentCategoryName(response, parentCategory.getCategoryName());
                }
            }
            
            log.info("分类创建成功: {} ({})", category.getCategoryCode(), category.getCategoryName());
            return ApiResponse.success("分类创建成功", response);
            
        } catch (Exception e) {
            log.error("创建分类异常: {}", request.getCategoryCode(), e);
            return ApiResponse.serverError("创建分类失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public ApiResponse<CategoryInfoResponse> updateCategory(Long categoryId, CategoryUpdateRequest request) {
        try {
            log.info("更新分类信息: {}", categoryId);
            
            // 查找分类
            Category existingCategory = categoryMapper.selectById(categoryId);
            if (existingCategory == null) {
                return ApiResponse.notFound("分类不存在");
            }

            // 检查分类代码是否已被其他分类使用
            if (!Objects.equals(request.getCategoryCode(), existingCategory.getCategoryCode())) {
                int count = categoryMapper.countByCategoryCode(request.getCategoryCode(), categoryId);
                if (count > 0) {
                    return ApiResponse.badRequest("分类代码已被其他分类使用");
                }
            }

            // 验证父分类变更
            if (!Objects.equals(request.getParentCategoryId(), existingCategory.getParentCategoryId())) {
                if (request.getParentCategoryId() != null) {
                    // 检查新父分类是否存在
                    Category newParentCategory = categoryMapper.selectById(request.getParentCategoryId());
                    if (newParentCategory == null) {
                        return ApiResponse.badRequest("新父分类不存在");
                    }
                    if (!newParentCategory.getIsActive()) {
                        return ApiResponse.badRequest("新父分类已禁用");
                    }
                    
                    // 检查是否会形成循环引用
                    if (isCircularReference(categoryId, request.getParentCategoryId())) {
                        return ApiResponse.badRequest("不能将分类移动到其子分类下");
                    }
                    
                    // 更新分类层级
                    if (request.getCategoryLevel() == null) {
                        request.setCategoryLevel(newParentCategory.getCategoryLevel() + 1);
                    }
                } else {
                    // 移动到顶级
                    if (request.getCategoryLevel() == null) {
                        request.setCategoryLevel(1);
                    }
                }
            }

            // 应用更新
            CategoryConverter.updateCategory(existingCategory, request);
            existingCategory.setUpdatedTime(LocalDateTime.now());

            // 保存更新
            int result = categoryMapper.updateById(existingCategory);
            if (result <= 0) {
                return ApiResponse.serverError("更新分类失败");
            }

            // 查询更新后的分类信息
            Category updatedCategory = categoryMapper.selectById(categoryId);
            CategoryInfoResponse response = CategoryConverter.toCategoryInfoResponse(updatedCategory);
            
            // 设置统计信息
            setResponseStatistics(response);
            
            log.info("分类信息更新成功: {} ({})", updatedCategory.getCategoryCode(), updatedCategory.getCategoryName());
            return ApiResponse.success("分类信息更新成功", response);
            
        } catch (Exception e) {
            log.error("更新分类信息异常: {}", categoryId, e);
            return ApiResponse.serverError("更新分类信息失败，请稍后重试");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteCategory(Long categoryId) {
        try {
            log.info("删除分类: {}", categoryId);
            
            // 查找分类
            Category category = categoryMapper.selectById(categoryId);
            if (category == null) {
                return ApiResponse.notFound("分类不存在");
            }

            // 检查是否有子分类
            List<Category> childCategories = categoryMapper.findByParentCategoryId(categoryId);
            if (!CollectionUtils.isEmpty(childCategories)) {
                return ApiResponse.badRequest("该分类下还有子分类，无法删除");
            }

            // 检查是否有图书使用该分类
            int bookCount = categoryMapper.countBooksByCategoryId(categoryId);
            if (bookCount > 0) {
                return ApiResponse.badRequest("该分类下还有图书，无法删除");
            }

            // 删除分类
            int result = categoryMapper.deleteById(categoryId);
            if (result <= 0) {
                return ApiResponse.serverError("删除分类失败");
            }
            
            log.info("分类删除成功: {} ({})", category.getCategoryCode(), category.getCategoryName());
            return ApiResponse.success("分类删除成功", null);
            
        } catch (Exception e) {
            log.error("删除分类异常: {}", categoryId, e);
            return ApiResponse.serverError("删除分类失败，请稍后重试");
        }
    }

    @Override
    public ApiResponse<CategoryInfoResponse> getCategoryById(Long categoryId) {
        try {
            Category category = categoryMapper.selectById(categoryId);
            if (category == null) {
                return ApiResponse.notFound("分类不存在");
            }

            CategoryInfoResponse response = CategoryConverter.toCategoryInfoResponse(category);
            setResponseStatistics(response);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("查询分类信息异常: {}", categoryId, e);
            return ApiResponse.serverError("查询分类信息失败");
        }
    }

    @Override
    public ApiResponse<CategoryInfoResponse> getCategoryByCode(String categoryCode) {
        try {
            Category category = categoryMapper.findByCategoryCode(categoryCode);
            if (category == null) {
                return ApiResponse.notFound("分类不存在");
            }

            CategoryInfoResponse response = CategoryConverter.toCategoryInfoResponse(category);
            setResponseStatistics(response);
            
            return ApiResponse.success(response);
            
        } catch (Exception e) {
            log.error("根据代码查询分类信息异常: {}", categoryCode, e);
            return ApiResponse.serverError("查询分类信息失败");
        }
    }

    @Override
    public ApiResponse<CategoryListResponse> getCategoryList(CategoryQueryRequest request) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<Category> queryWrapper = buildQueryWrapper(request);
            
            // 分页查询
            Page<Category> page = new Page<>(request.getPageNum(), request.getPageSize());
            IPage<Category> categoryPage = categoryMapper.selectPage(page, queryWrapper);
            
            // 转换为响应对象
            List<CategorySimpleResponse> categoryResponses = CategoryConverter.toCategorySimpleResponseList(categoryPage.getRecords());
            
            // 设置统计信息
            if (request.getIncludeChildCount() || request.getIncludeBookCount()) {
                for (CategorySimpleResponse response : categoryResponses) {
                    if (request.getIncludeChildCount()) {
                        List<Category> children = categoryMapper.findByParentCategoryId(response.getCategoryId());
                        CategoryConverter.setCategoryStatistics(response, children.size(), null);
                    }
                    if (request.getIncludeBookCount()) {
                        int bookCount = categoryMapper.countBooksByCategoryId(response.getCategoryId());
                        CategoryConverter.setCategoryStatistics(response, null, bookCount);
                    }
                }
            }
            
            // 构建分页响应
            CategoryListResponse listResponse = CategoryListResponse.builder()
                    .categories(categoryResponses)
                    .total(categoryPage.getTotal())
                    .pageNum(request.getPageNum())
                    .pageSize(request.getPageSize())
                    .totalPages((int) categoryPage.getPages())
                    .hasPrevious(categoryPage.getCurrent() > 1)
                    .hasNext(categoryPage.getCurrent() < categoryPage.getPages())
                    .isFirst(categoryPage.getCurrent() == 1)
                    .isLast(categoryPage.getCurrent() == categoryPage.getPages())
                    .build();
            
            return ApiResponse.success(listResponse);
            
        } catch (Exception e) {
            log.error("查询分类列表异常", e);
            return ApiResponse.serverError("查询分类列表失败");
        }
    }

    @Override
    public ApiResponse<List<CategorySimpleResponse>> getCategorySimpleList(Boolean isActive, Long parentCategoryId) {
        try {
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            
            if (isActive != null) {
                queryWrapper.eq(Category::getIsActive, isActive);
            }
            
            if (parentCategoryId != null) {
                queryWrapper.eq(Category::getParentCategoryId, parentCategoryId);
            } else {
                queryWrapper.isNull(Category::getParentCategoryId);
            }
            
            queryWrapper.orderByAsc(Category::getSortOrder, Category::getCategoryCode);
            
            List<Category> categories = categoryMapper.selectList(queryWrapper);
            List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(categories);
            
            return ApiResponse.success(responses);
            
        } catch (Exception e) {
            log.error("查询分类简要列表异常", e);
            return ApiResponse.serverError("查询分类列表失败");
        }
    }

    @Override
    public ApiResponse<List<CategorySimpleResponse>> getTopLevelCategories() {
        try {
            List<Category> categories = categoryMapper.findTopLevelCategories();
            List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(categories);
            
            return ApiResponse.success(responses);
            
        } catch (Exception e) {
            log.error("查询顶级分类异常", e);
            return ApiResponse.serverError("查询顶级分类失败");
        }
    }

    @Override
    public ApiResponse<List<CategorySimpleResponse>> getChildCategories(Long parentCategoryId) {
        try {
            List<Category> categories = categoryMapper.findByParentCategoryId(parentCategoryId);
            List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(categories);
            
            return ApiResponse.success(responses);
            
        } catch (Exception e) {
            log.error("查询子分类异常: {}", parentCategoryId, e);
            return ApiResponse.serverError("查询子分类失败");
        }
    }

    @Override
    public ApiResponse<List<CategoryTreeResponse>> getCategoryTree(Boolean includeInactive) {
        try {
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            
            if (includeInactive == null || !includeInactive) {
                queryWrapper.eq(Category::getIsActive, true);
            }
            
            queryWrapper.orderByAsc(Category::getCategoryLevel, Category::getSortOrder, Category::getCategoryCode);
            
            List<Category> categories = categoryMapper.selectList(queryWrapper);
            List<CategoryTreeResponse> treeResponses = CategoryConverter.buildCategoryTree(categories);
            
            return ApiResponse.success(treeResponses);
            
        } catch (Exception e) {
            log.error("查询分类树异常", e);
            return ApiResponse.serverError("查询分类树失败");
        }
    }

    @Override
    public ApiResponse<List<CategorySimpleResponse>> getCategoryPath(Long categoryId) {
        try {
            List<Category> pathCategories = categoryMapper.findCategoryPath(categoryId);
            List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(pathCategories);
            
            return ApiResponse.success(responses);
            
        } catch (Exception e) {
            log.error("查询分类路径异常: {}", categoryId, e);
            return ApiResponse.serverError("查询分类路径失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> batchUpdateCategories(CategoryBatchUpdateRequest request) {
        try {
            log.info("批量更新分类: {} 个分类", request.getCategoryIds().size());
            
            List<Long> categoryIds = request.getCategoryIds();
            String operationType = request.getOperationType();
            
            switch (operationType) {
                case "enable":
                    return batchUpdateActiveStatus(categoryIds, true);
                case "disable":
                    return batchUpdateActiveStatus(categoryIds, false);
                case "delete":
                    return batchDeleteCategories(categoryIds);
                case "updateSort":
                    return batchUpdateSortOrder(categoryIds, request.getSortOrderStart());
                case "move":
                    return batchMoveCategories(categoryIds, request.getNewParentCategoryId());
                default:
                    return ApiResponse.badRequest("不支持的操作类型");
            }
            
        } catch (Exception e) {
            log.error("批量更新分类异常", e);
            return ApiResponse.serverError("批量更新分类失败");
        }
    }

    @Override
    public ApiResponse<CategoryStatisticsResponse> getCategoryStatistics() {
        try {
            CategoryStatisticsResponse statistics = new CategoryStatisticsResponse();
            
            // 基础统计
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            long totalCategories = categoryMapper.selectCount(queryWrapper);
            
            queryWrapper.clear();
            queryWrapper.eq(Category::getIsActive, true);
            long activeCategories = categoryMapper.selectCount(queryWrapper);
            
            queryWrapper.clear();
            queryWrapper.eq(Category::getIsActive, false);
            long inactiveCategories = categoryMapper.selectCount(queryWrapper);
            
            queryWrapper.clear();
            queryWrapper.isNull(Category::getParentCategoryId).eq(Category::getIsActive, true);
            long topLevelCategories = categoryMapper.selectCount(queryWrapper);
            
            List<Category> categoriesWithBooks = categoryMapper.findCategoriesWithBooks();
            long categoriesWithBooksCount = categoriesWithBooks.size();
            
            // 设置基础统计
            statistics.setTotalCategories(totalCategories);
            statistics.setActiveCategories(activeCategories);
            statistics.setInactiveCategories(inactiveCategories);
            statistics.setTopLevelCategories(topLevelCategories);
            statistics.setCategoriesWithBooks(categoriesWithBooksCount);
            statistics.setEmptyCategoriesCount(activeCategories - categoriesWithBooksCount);
            
            // 按层级统计
            List<Category> levelStats = categoryMapper.countByCategoryLevel();
            Map<Integer, Long> categoriesByLevel = levelStats.stream()
                    .collect(Collectors.toMap(
                            Category::getCategoryLevel,
                            c -> Long.valueOf(c.getCategoryId()) // 这里复用了categoryId字段存储count
                    ));
            statistics.setCategoriesByLevel(categoriesByLevel);
            
            // 按状态统计
            Map<String, Long> categoriesByStatus = new HashMap<>();
            categoriesByStatus.put("active", activeCategories);
            categoriesByStatus.put("inactive", inactiveCategories);
            statistics.setCategoriesByStatus(categoriesByStatus);
            
            // 最大层级
            if (!categoriesByLevel.isEmpty()) {
                statistics.setMaxCategoryLevel(Collections.max(categoriesByLevel.keySet()));
            } else {
                statistics.setMaxCategoryLevel(0);
            }
            
            // 最近创建的分类
            queryWrapper.clear();
            queryWrapper.eq(Category::getIsActive, true)
                    .orderByDesc(Category::getCreatedTime)
                    .last("LIMIT 10");
            List<Category> recentCategories = categoryMapper.selectList(queryWrapper);
            statistics.setRecentCategories(CategoryConverter.toCategorySimpleResponseList(recentCategories));
            
            return ApiResponse.success(statistics);
            
        } catch (Exception e) {
            log.error("获取分类统计信息异常", e);
            return ApiResponse.serverError("获取统计信息失败");
        }
    }

    @Override
    public ApiResponse<Boolean> checkCategoryCodeAvailable(String categoryCode, Long excludeCategoryId) {
        try {
            int count = categoryMapper.countByCategoryCode(categoryCode, excludeCategoryId);
            return ApiResponse.success(count == 0);
            
        } catch (Exception e) {
            log.error("检查分类代码可用性异常: {}", categoryCode, e);
            return ApiResponse.serverError("检查分类代码失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> enableCategory(Long categoryId) {
        return updateCategoryActiveStatus(categoryId, true, "启用");
    }

    @Override
    @Transactional
    public ApiResponse<Void> disableCategory(Long categoryId) {
        return updateCategoryActiveStatus(categoryId, false, "禁用");
    }

    @Override
    @Transactional
    public ApiResponse<Void> moveCategory(Long categoryId, Long newParentCategoryId) {
        try {
            log.info("移动分类: {} 到父分类: {}", categoryId, newParentCategoryId);
            
            Category category = categoryMapper.selectById(categoryId);
            if (category == null) {
                return ApiResponse.notFound("分类不存在");
            }

            // 验证新父分类
            if (newParentCategoryId != null) {
                Category newParentCategory = categoryMapper.selectById(newParentCategoryId);
                if (newParentCategory == null) {
                    return ApiResponse.badRequest("新父分类不存在");
                }
                if (!newParentCategory.getIsActive()) {
                    return ApiResponse.badRequest("新父分类已禁用");
                }
                
                // 检查循环引用
                if (isCircularReference(categoryId, newParentCategoryId)) {
                    return ApiResponse.badRequest("不能将分类移动到其子分类下");
                }
                
                // 更新层级
                category.setCategoryLevel(newParentCategory.getCategoryLevel() + 1);
            } else {
                // 移动到顶级
                category.setCategoryLevel(1);
            }

            category.setParentCategoryId(newParentCategoryId);
            category.setUpdatedTime(LocalDateTime.now());
            
            int result = categoryMapper.updateById(category);
            if (result <= 0) {
                return ApiResponse.serverError("移动分类失败");
            }
            
            log.info("分类移动成功: {}", categoryId);
            return ApiResponse.success("分类移动成功", null);
            
        } catch (Exception e) {
            log.error("移动分类异常: {}", categoryId, e);
            return ApiResponse.serverError("移动分类失败");
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> updateCategorySortOrder(Long categoryId, Integer sortOrder) {
        try {
            int result = categoryMapper.updateSortOrder(categoryId, sortOrder);
            if (result <= 0) {
                return ApiResponse.notFound("分类不存在");
            }
            
            return ApiResponse.success("排序更新成功", null);
            
        } catch (Exception e) {
            log.error("更新分类排序异常: {}", categoryId, e);
            return ApiResponse.serverError("更新排序失败");
        }
    }

    @Override
    public ApiResponse<List<CategorySimpleResponse>> getCategoriesWithBooks() {
        try {
            List<Category> categories = categoryMapper.findCategoriesWithBooks();
            List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(categories);
            
            return ApiResponse.success(responses);
            
        } catch (Exception e) {
            log.error("查询有图书的分类异常", e);
            return ApiResponse.serverError("查询分类失败");
        }
    }

    @Override
    public ApiResponse<List<CategorySimpleResponse>> getCategoriesByLevel(Integer categoryLevel) {
        try {
            List<Category> categories = categoryMapper.findByCategoryLevel(categoryLevel);
            List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(categories);
            
            return ApiResponse.success(responses);
            
        } catch (Exception e) {
            log.error("根据层级查询分类异常: {}", categoryLevel, e);
            return ApiResponse.serverError("查询分类失败");
        }
    }

    @Override
    public ApiResponse<List<CategorySimpleResponse>> searchCategories(String categoryName) {
        try {
            List<Category> categories = categoryMapper.findByCategoryNameLike(categoryName);
            List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(categories);
            
            return ApiResponse.success(responses);
            
        } catch (Exception e) {
            log.error("搜索分类异常: {}", categoryName, e);
            return ApiResponse.serverError("搜索分类失败");
        }
    }

    @Override
    public Category validateCategory(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null || !category.getIsActive()) {
            return null;
        }
        return category;
    }

    @Override
    @Transactional
    public void updateCategoryBookCount(Long categoryId, Integer bookCount) {
        try {
            // 这里可以实现图书数量的更新逻辑
            // 由于当前没有在Category表中存储图书数量字段，这里暂时不实现
            log.info("更新分类图书统计: categoryId={}, bookCount={}", categoryId, bookCount);
        } catch (Exception e) {
            log.error("更新分类图书统计异常: {}", categoryId, e);
        }
    }

    @Override
    public ApiResponse<List<Long>> getCategoryWithChildrenIds(Long categoryId) {
        try {
            List<Long> categoryIds = categoryMapper.findCategoryIdWithChildren(categoryId);
            return ApiResponse.success(categoryIds);
            
        } catch (Exception e) {
            log.error("查询分类及子分类ID异常: {}", categoryId, e);
            return ApiResponse.serverError("查询分类ID失败");
        }
    }

    @Override
    public ApiResponse<Boolean> checkCategoryDeletable(Long categoryId) {
        try {
            // 检查是否有子分类
            List<Category> children = categoryMapper.findByParentCategoryId(categoryId);
            if (!CollectionUtils.isEmpty(children)) {
                return ApiResponse.success(false);
            }
            
            // 检查是否有图书
            int bookCount = categoryMapper.countBooksByCategoryId(categoryId);
            return ApiResponse.success(bookCount == 0);
            
        } catch (Exception e) {
            log.error("检查分类是否可删除异常: {}", categoryId, e);
            return ApiResponse.serverError("检查失败");
        }
    }

    @Override
    public ApiResponse<Integer> getMaxSortOrder(Long parentCategoryId) {
        try {
            Integer maxSortOrder = categoryMapper.findMaxSortOrder(parentCategoryId);
            return ApiResponse.success(maxSortOrder != null ? maxSortOrder : 0);
            
        } catch (Exception e) {
            log.error("获取最大排序值异常: {}", parentCategoryId, e);
            return ApiResponse.serverError("获取排序值失败");
        }
    }

    // =============== 私有辅助方法 ===============

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Category> buildQueryWrapper(CategoryQueryRequest request) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        
        // 分类代码精确匹配
        if (StringUtils.hasText(request.getCategoryCode())) {
            queryWrapper.eq(Category::getCategoryCode, request.getCategoryCode());
        }
        
        // 分类名称模糊匹配
        if (StringUtils.hasText(request.getCategoryName())) {
            queryWrapper.like(Category::getCategoryName, request.getCategoryName());
        }
        
        // 父分类ID
        if (request.getParentCategoryId() != null) {
            queryWrapper.eq(Category::getParentCategoryId, request.getParentCategoryId());
        }
        
        // 是否查询顶级分类
        if (request.getIsTopLevel() != null && request.getIsTopLevel()) {
            queryWrapper.isNull(Category::getParentCategoryId);
        }
        
        // 分类层级
        if (request.getCategoryLevel() != null) {
            queryWrapper.eq(Category::getCategoryLevel, request.getCategoryLevel());
        }
        
        // 启用状态
        if (request.getIsActive() != null) {
            queryWrapper.eq(Category::getIsActive, request.getIsActive());
        }
        
        // 关键词搜索
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Category::getCategoryCode, request.getKeyword())
                    .or()
                    .like(Category::getCategoryName, request.getKeyword())
            );
        }
        
        // 排序
        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();
        
        if ("category_code".equals(sortField)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(Category::getCategoryCode);
            } else {
                queryWrapper.orderByAsc(Category::getCategoryCode);
            }
        } else if ("category_name".equals(sortField)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(Category::getCategoryName);
            } else {
                queryWrapper.orderByAsc(Category::getCategoryName);
            }
        } else if ("category_level".equals(sortField)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(Category::getCategoryLevel);
            } else {
                queryWrapper.orderByAsc(Category::getCategoryLevel);
            }
        } else if ("created_time".equals(sortField)) {
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(Category::getCreatedTime);
            } else {
                queryWrapper.orderByAsc(Category::getCreatedTime);
            }
        } else {
            // 默认按排序字段升序
            if ("desc".equals(sortOrder)) {
                queryWrapper.orderByDesc(Category::getSortOrder);
            } else {
                queryWrapper.orderByAsc(Category::getSortOrder);
            }
        }
        
        return queryWrapper;
    }

    /**
     * 设置响应对象的统计信息
     */
    private void setResponseStatistics(CategoryInfoResponse response) {
        if (response == null) return;
        
        try {
            // 设置子分类数量
            List<Category> children = categoryMapper.findByParentCategoryId(response.getCategoryId());
            int childCount = children.size();
            
            // 设置图书数量
            int bookCount = categoryMapper.countBooksByCategoryId(response.getCategoryId());
            
            // 设置父分类名称
            if (response.getParentCategoryId() != null) {
                Category parentCategory = categoryMapper.selectById(response.getParentCategoryId());
                if (parentCategory != null) {
                    CategoryConverter.setParentCategoryName(response, parentCategory.getCategoryName());
                }
            }
            
            // 设置统计信息
            CategoryConverter.setCategoryStatistics(response, childCount, bookCount);
            
        } catch (Exception e) {
            log.warn("设置分类统计信息失败: {}", response.getCategoryId(), e);
        }
    }

    /**
     * 检查是否会形成循环引用
     */
    private boolean isCircularReference(Long categoryId, Long newParentCategoryId) {
        if (newParentCategoryId == null) {
            return false;
        }
        
        // 如果新父分类就是当前分类，直接返回true
        if (Objects.equals(categoryId, newParentCategoryId)) {
            return true;
        }
        
        // 检查新父分类的所有祖先分类中是否包含当前分类
        List<Long> childrenIds = categoryMapper.findCategoryIdWithChildren(categoryId);
        return childrenIds.contains(newParentCategoryId);
    }

    /**
     * 更新分类启用状态
     */
    private ApiResponse<Void> updateCategoryActiveStatus(Long categoryId, Boolean isActive, String operation) {
        try {
            log.info("{}分类: {}", operation, categoryId);
            
            int result = categoryMapper.updateActiveStatus(categoryId, isActive);
            if (result <= 0) {
                return ApiResponse.notFound("分类不存在");
            }
            
            log.info("分类{}成功: {}", operation, categoryId);
            return ApiResponse.success("分类" + operation + "成功", null);
            
        } catch (Exception e) {
            log.error("{}分类异常: {}", operation, categoryId, e);
            return ApiResponse.serverError(operation + "分类失败");
        }
    }

    /**
     * 批量更新启用状态
     */
    private ApiResponse<Void> batchUpdateActiveStatus(List<Long> categoryIds, Boolean isActive) {
        try {
            LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(Category::getCategoryId, categoryIds)
                    .set(Category::getIsActive, isActive)
                    .set(Category::getUpdatedTime, LocalDateTime.now());
            
            int result = categoryMapper.update(null, updateWrapper);
            if (result <= 0) {
                return ApiResponse.serverError("批量更新状态失败");
            }
            
            String operation = isActive ? "启用" : "禁用";
            log.info("批量{}分类成功: {} 个", operation, result);
            return ApiResponse.success("批量" + operation + "成功", null);
            
        } catch (Exception e) {
            log.error("批量更新分类状态异常", e);
            return ApiResponse.serverError("批量更新状态失败");
        }
    }

    /**
     * 批量删除分类
     */
    private ApiResponse<Void> batchDeleteCategories(List<Long> categoryIds) {
        try {
            // 检查每个分类是否可以删除
            for (Long categoryId : categoryIds) {
                ApiResponse<Boolean> checkResult = checkCategoryDeletable(categoryId);
                if (!checkResult.getSuccess() || !checkResult.getData()) {
                    Category category = categoryMapper.selectById(categoryId);
                    String categoryName = category != null ? category.getCategoryName() : categoryId.toString();
                    return ApiResponse.badRequest("分类 " + categoryName + " 不能删除");
                }
            }
            
            // 执行删除
            int result = categoryMapper.deleteBatchIds(categoryIds);
            if (result <= 0) {
                return ApiResponse.serverError("批量删除失败");
            }
            
            log.info("批量删除分类成功: {} 个", result);
            return ApiResponse.success("批量删除成功", null);
            
        } catch (Exception e) {
            log.error("批量删除分类异常", e);
            return ApiResponse.serverError("批量删除失败");
        }
    }

    /**
     * 批量更新排序
     */
    private ApiResponse<Void> batchUpdateSortOrder(List<Long> categoryIds, Integer sortOrderStart) {
        try {
            if (sortOrderStart == null) {
                sortOrderStart = 1;
            }
            
            for (int i = 0; i < categoryIds.size(); i++) {
                Long categoryId = categoryIds.get(i);
                Integer sortOrder = sortOrderStart + i;
                categoryMapper.updateSortOrder(categoryId, sortOrder);
            }
            
            log.info("批量更新分类排序成功: {} 个", categoryIds.size());
            return ApiResponse.success("批量更新排序成功", null);
            
        } catch (Exception e) {
            log.error("批量更新分类排序异常", e);
            return ApiResponse.serverError("批量更新排序失败");
        }
    }

    /**
     * 批量移动分类
     */
    private ApiResponse<Void> batchMoveCategories(List<Long> categoryIds, Long newParentCategoryId) {
        try {
            // 验证新父分类
            Integer newLevel = 1;
            if (newParentCategoryId != null) {
                Category newParentCategory = categoryMapper.selectById(newParentCategoryId);
                if (newParentCategory == null) {
                    return ApiResponse.badRequest("新父分类不存在");
                }
                if (!newParentCategory.getIsActive()) {
                    return ApiResponse.badRequest("新父分类已禁用");
                }
                newLevel = newParentCategory.getCategoryLevel() + 1;
            }
            
            // 检查循环引用
            for (Long categoryId : categoryIds) {
                if (isCircularReference(categoryId, newParentCategoryId)) {
                    Category category = categoryMapper.selectById(categoryId);
                    String categoryName = category != null ? category.getCategoryName() : categoryId.toString();
                    return ApiResponse.badRequest("分类 " + categoryName + " 不能移动到其子分类下");
                }
            }
            
            // 执行移动
            LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(Category::getCategoryId, categoryIds)
                    .set(Category::getParentCategoryId, newParentCategoryId)
                    .set(Category::getCategoryLevel, newLevel)
                    .set(Category::getUpdatedTime, LocalDateTime.now());
            
            int result = categoryMapper.update(null, updateWrapper);
            if (result <= 0) {
                return ApiResponse.serverError("批量移动失败");
            }
            
            log.info("批量移动分类成功: {} 个", result);
            return ApiResponse.success("批量移动成功", null);
            
        } catch (Exception e) {
            log.error("批量移动分类异常", e);
            return ApiResponse.serverError("批量移动失败");
        }
    }
} 