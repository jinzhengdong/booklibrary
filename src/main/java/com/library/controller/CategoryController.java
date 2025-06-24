package com.library.controller;

import com.library.dto.request.*;
import com.library.dto.response.*;
import com.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 分类管理控制器
 * 提供分类相关的RESTful API接口
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:8081", "http://127.0.0.1:8081", "http://localhost", "http://127.0.0.1"}, maxAge = 3600)
public class CategoryController {

    private final CategoryService categoryService;



    // ========================================
    // 分类CRUD操作接口
    // ========================================

    /**
     * 创建分类
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryInfoResponse>> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        log.info("创建分类请求: {}", request.getCategoryCode());
        ApiResponse<CategoryInfoResponse> response = categoryService.createCategory(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据ID获取分类详细信息
     * GET /api/categories/{categoryId}
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryInfoResponse>> getCategoryById(@PathVariable Long categoryId) {
        log.debug("获取分类信息: {}", categoryId);
        ApiResponse<CategoryInfoResponse> response = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新分类信息
     * PUT /api/categories/{categoryId}
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryInfoResponse>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest request) {
        log.info("更新分类信息: {}", categoryId);
        ApiResponse<CategoryInfoResponse> response = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除分类
     * DELETE /api/categories/{categoryId}
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        log.info("删除分类: {}", categoryId);
        ApiResponse<Void> response = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 分类查询接口
    // ========================================

    /**
     * 分页查询分类列表
     * GET /api/categories?pageNum=1&pageSize=10&categoryLevel=1&isActive=true&keyword=计算机
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CategoryListResponse>> getCategoryList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryLevel,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long parentCategoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "sort_order") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "false") Boolean includeStatistics) {
        
        CategoryQueryRequest request = CategoryQueryRequest.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .categoryLevel(categoryLevel)
                .isActive(isActive)
                .parentCategoryId(parentCategoryId)
                .keyword(keyword)
                .sortField(sortBy)
                .sortOrder(sortDir)
                .includeChildCount(includeStatistics)
                .includeBookCount(includeStatistics)
                .build();
        
        log.debug("分页查询分类列表: {}", request);
        ApiResponse<CategoryListResponse> response = categoryService.getCategoryList(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取分类简要信息列表（不分页）
     * GET /api/categories/simple?isActive=true&parentCategoryId=1
     */
    @GetMapping("/simple")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getCategorySimpleList(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long parentCategoryId) {
        log.debug("获取分类简要列表: isActive={}, parentCategoryId={}", isActive, parentCategoryId);
        ApiResponse<List<CategorySimpleResponse>> response = categoryService.getCategorySimpleList(isActive, parentCategoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据分类代码获取分类信息
     * GET /api/categories/code/{categoryCode}
     */
    @GetMapping("/code/{categoryCode}")
    public ResponseEntity<ApiResponse<CategoryInfoResponse>> getCategoryByCode(@PathVariable String categoryCode) {
        log.debug("根据分类代码获取分类信息: {}", categoryCode);
        ApiResponse<CategoryInfoResponse> response = categoryService.getCategoryByCode(categoryCode);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 分类层级结构接口
    // ========================================

    /**
     * 获取顶级分类列表
     * GET /api/categories/top-level
     */
    @GetMapping("/top-level")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getTopLevelCategories() {
        log.debug("获取顶级分类列表");
        ApiResponse<List<CategorySimpleResponse>> response = categoryService.getTopLevelCategories();
        return ResponseEntity.ok(response);
    }

    /**
     * 根据父分类ID获取子分类列表
     * GET /api/categories/children/{parentCategoryId}
     */
    @GetMapping("/children/{parentCategoryId}")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getChildCategories(
            @PathVariable Long parentCategoryId) {
        log.debug("根据父分类获取子分类列表: {}", parentCategoryId);
        ApiResponse<List<CategorySimpleResponse>> response = categoryService.getChildCategories(parentCategoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取分类树结构
     * GET /api/categories/tree?includeInactive=false
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> getCategoryTree(
            @RequestParam(defaultValue = "false") Boolean includeInactive) {
        log.debug("获取分类树结构: includeInactive={}", includeInactive);
        ApiResponse<List<CategoryTreeResponse>> response = categoryService.getCategoryTree(includeInactive);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取分类路径（从根分类到指定分类）
     * GET /api/categories/{categoryId}/path
     */
    @GetMapping("/{categoryId}/path")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getCategoryPath(@PathVariable Long categoryId) {
        log.debug("获取分类路径: {}", categoryId);
        ApiResponse<List<CategorySimpleResponse>> response = categoryService.getCategoryPath(categoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 根据分类层级获取分类列表
     * GET /api/categories/level/{categoryLevel}
     */
    @GetMapping("/level/{categoryLevel}")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getCategoriesByLevel(
            @PathVariable Integer categoryLevel) {
        log.debug("根据层级获取分类列表: {}", categoryLevel);
        ApiResponse<List<CategorySimpleResponse>> response = categoryService.getCategoriesByLevel(categoryLevel);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 分类搜索接口
    // ========================================

    /**
     * 搜索分类（按名称模糊匹配）
     * GET /api/categories/search?categoryName=计算机
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> searchCategories(
            @RequestParam String categoryName) {
        log.debug("搜索分类: {}", categoryName);
        ApiResponse<List<CategorySimpleResponse>> response = categoryService.searchCategories(categoryName);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取有图书的分类列表
     * GET /api/categories/with-books
     */
    @GetMapping("/with-books")
    public ResponseEntity<ApiResponse<List<CategorySimpleResponse>>> getCategoriesWithBooks() {
        log.debug("获取有图书的分类列表");
        ApiResponse<List<CategorySimpleResponse>> response = categoryService.getCategoriesWithBooks();
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 分类状态管理接口
    // ========================================

    /**
     * 启用分类
     * POST /api/categories/{categoryId}/enable
     */
    @PostMapping("/{categoryId}/enable")
    public ResponseEntity<ApiResponse<Void>> enableCategory(@PathVariable Long categoryId) {
        log.info("启用分类: {}", categoryId);
        ApiResponse<Void> response = categoryService.enableCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 禁用分类
     * POST /api/categories/{categoryId}/disable
     */
    @PostMapping("/{categoryId}/disable")
    public ResponseEntity<ApiResponse<Void>> disableCategory(@PathVariable Long categoryId) {
        log.info("禁用分类: {}", categoryId);
        ApiResponse<Void> response = categoryService.disableCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 批量更新分类
     * POST /api/categories/batch-update
     */
    @PostMapping("/batch-update")
    public ResponseEntity<ApiResponse<Void>> batchUpdateCategories(@Valid @RequestBody CategoryBatchUpdateRequest request) {
        log.info("批量更新分类: {} -> {}", request.getCategoryIds(), request.getOperationType());
        ApiResponse<Void> response = categoryService.batchUpdateCategories(request);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 分类管理接口
    // ========================================

    /**
     * 移动分类到新的父分类下
     * POST /api/categories/{categoryId}/move?newParentCategoryId=2
     */
    @PostMapping("/{categoryId}/move")
    public ResponseEntity<ApiResponse<Void>> moveCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) Long newParentCategoryId) {
        log.info("移动分类: {} -> {}", categoryId, newParentCategoryId);
        ApiResponse<Void> response = categoryService.moveCategory(categoryId, newParentCategoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 更新分类排序
     * POST /api/categories/{categoryId}/sort?sortOrder=5
     */
    @PostMapping("/{categoryId}/sort")
    public ResponseEntity<ApiResponse<Void>> updateCategorySortOrder(
            @PathVariable Long categoryId,
            @RequestParam Integer sortOrder) {
        log.info("更新分类排序: {} -> {}", categoryId, sortOrder);
        ApiResponse<Void> response = categoryService.updateCategorySortOrder(categoryId, sortOrder);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 数据验证接口
    // ========================================

    /**
     * 检查分类代码是否可用
     * GET /api/categories/check-code?categoryCode=CS001&excludeCategoryId=1
     */
    @GetMapping("/check-code")
    public ResponseEntity<ApiResponse<Boolean>> checkCategoryCodeAvailable(
            @RequestParam String categoryCode,
            @RequestParam(required = false) Long excludeCategoryId) {
        log.debug("检查分类代码可用性: {}, 排除分类: {}", categoryCode, excludeCategoryId);
        ApiResponse<Boolean> response = categoryService.checkCategoryCodeAvailable(categoryCode, excludeCategoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 检查分类是否可以删除
     * GET /api/categories/{categoryId}/deletable
     */
    @GetMapping("/{categoryId}/deletable")
    public ResponseEntity<ApiResponse<Boolean>> checkCategoryDeletable(@PathVariable Long categoryId) {
        log.debug("检查分类是否可删除: {}", categoryId);
        ApiResponse<Boolean> response = categoryService.checkCategoryDeletable(categoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取分类的最大排序值
     * GET /api/categories/max-sort-order?parentCategoryId=1
     */
    @GetMapping("/max-sort-order")
    public ResponseEntity<ApiResponse<Integer>> getMaxSortOrder(
            @RequestParam(required = false) Long parentCategoryId) {
        log.debug("获取最大排序值: parentCategoryId={}", parentCategoryId);
        ApiResponse<Integer> response = categoryService.getMaxSortOrder(parentCategoryId);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 分类关系接口
    // ========================================

    /**
     * 获取分类及其所有子分类的ID列表
     * GET /api/categories/{categoryId}/with-children-ids
     */
    @GetMapping("/{categoryId}/with-children-ids")
    public ResponseEntity<ApiResponse<List<Long>>> getCategoryWithChildrenIds(@PathVariable Long categoryId) {
        log.debug("获取分类及子分类ID列表: {}", categoryId);
        ApiResponse<List<Long>> response = categoryService.getCategoryWithChildrenIds(categoryId);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 统计信息接口
    // ========================================

    /**
     * 获取分类统计信息
     * GET /api/categories/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<CategoryStatisticsResponse>> getCategoryStatistics() {
        log.debug("获取分类统计信息");
        ApiResponse<CategoryStatisticsResponse> response = categoryService.getCategoryStatistics();
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 健康检查接口
    // ========================================

    /**
     * 分类模块健康检查
     * GET /api/categories/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        log.debug("分类模块健康检查");
        return ResponseEntity.ok(ApiResponse.success("分类模块运行正常"));
    }
}