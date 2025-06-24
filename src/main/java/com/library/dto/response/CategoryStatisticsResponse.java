package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 分类统计响应DTO
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatisticsResponse {

    /**
     * 总分类数
     */
    private Long totalCategories;

    /**
     * 启用分类数
     */
    private Long activeCategories;

    /**
     * 禁用分类数
     */
    private Long inactiveCategories;

    /**
     * 顶级分类数
     */
    private Long topLevelCategories;

    /**
     * 叶子分类数（没有子分类的分类）
     */
    private Long leafCategories;

    /**
     * 有图书的分类数
     */
    private Long categoriesWithBooks;

    /**
     * 空分类数（没有图书的分类）
     */
    private Long emptyCategoriesCount;

    /**
     * 最大分类层级
     */
    private Integer maxCategoryLevel;

    /**
     * 平均每个分类的图书数
     */
    private Double averageBooksPerCategory;

    /**
     * 按层级统计分类数量
     */
    private Map<Integer, Long> categoriesByLevel;

    /**
     * 按状态统计分类数量
     */
    private Map<String, Long> categoriesByStatus;

    /**
     * 图书数量最多的前10个分类
     */
    private List<CategoryBookCountResponse> topCategoriesByBookCount;

    /**
     * 最近创建的分类
     */
    private List<CategorySimpleResponse> recentCategories;

    /**
     * 本月新增分类数
     */
    private Long monthlyNewCategories;

    /**
     * 本年新增分类数
     */
    private Long yearlyNewCategories;

    /**
     * 分类树深度统计
     */
    private Map<Integer, Long> treeDepthStatistics;

    /**
     * 分类图书数量统计内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBookCountResponse {
        
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
         * 图书数量
         */
        private Long bookCount;
        
        /**
         * 分类层级
         */
        private Integer categoryLevel;
    }
} 