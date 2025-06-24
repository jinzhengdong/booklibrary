package com.library.dto;

import com.library.dto.converter.CategoryConverter;
import com.library.dto.request.CategoryCreateRequest;
import com.library.dto.response.CategoryInfoResponse;
import com.library.dto.response.CategorySimpleResponse;
import com.library.entity.Category;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分类DTO转换测试类
 * 
 * @author Library Management System
 * @version 1.0.0
 */
public class CategoryDTOTest {

    @Test
    public void testCategoryCreateRequestToEntity() {
        // 准备测试数据
        CategoryCreateRequest request = new CategoryCreateRequest();
        request.setCategoryCode("TP");
        request.setCategoryName("计算机技术");
        request.setParentCategoryId(1L);
        request.setCategoryLevel(2);
        request.setSortOrder(10);
        request.setDescription("计算机相关技术");
        request.setIsActive(true);

        // 执行转换
        Category category = CategoryConverter.toCategory(request);

        // 验证转换结果
        assertNotNull(category);
        assertEquals("TP", category.getCategoryCode());
        assertEquals("计算机技术", category.getCategoryName());
        assertEquals(1L, category.getParentCategoryId());
        assertEquals(2, category.getCategoryLevel());
        assertEquals(10, category.getSortOrder());
        assertEquals("计算机相关技术", category.getDescription());
        assertTrue(category.getIsActive());
    }

    @Test
    public void testEntityToCategoryInfoResponse() {
        // 准备测试数据
        Category category = Category.builder()
                .categoryId(1L)
                .categoryCode("TP")
                .categoryName("计算机技术")
                .parentCategoryId(null)
                .categoryLevel(1)
                .sortOrder(10)
                .description("计算机相关技术")
                .isActive(true)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        // 执行转换
        CategoryInfoResponse response = CategoryConverter.toCategoryInfoResponse(category);

        // 验证转换结果
        assertNotNull(response);
        assertEquals(1L, response.getCategoryId());
        assertEquals("TP", response.getCategoryCode());
        assertEquals("计算机技术", response.getCategoryName());
        assertNull(response.getParentCategoryId());
        assertEquals(1, response.getCategoryLevel());
        assertEquals(10, response.getSortOrder());
        assertEquals("计算机相关技术", response.getDescription());
        assertTrue(response.getIsActive());
        assertEquals("启用", response.getIsActiveText());
        assertEquals(0, response.getDepth());
        assertNotNull(response.getCreatedTime());
        assertNotNull(response.getUpdatedTime());
    }

    @Test
    public void testEntityToCategorySimpleResponse() {
        // 准备测试数据
        Category category = Category.builder()
                .categoryId(2L)
                .categoryCode("TP31")
                .categoryName("计算机软件")
                .parentCategoryId(1L)
                .categoryLevel(2)
                .sortOrder(5)
                .isActive(false)
                .createdTime(LocalDateTime.now())
                .build();

        // 执行转换
        CategorySimpleResponse response = CategoryConverter.toCategorySimpleResponse(category);

        // 验证转换结果
        assertNotNull(response);
        assertEquals(2L, response.getCategoryId());
        assertEquals("TP31", response.getCategoryCode());
        assertEquals("计算机软件", response.getCategoryName());
        assertEquals(1L, response.getParentCategoryId());
        assertEquals(2, response.getCategoryLevel());
        assertEquals(5, response.getSortOrder());
        assertFalse(response.getIsActive());
        assertEquals("禁用", response.getIsActiveText());
        assertNotNull(response.getCreatedTime());
    }

    @Test
    public void testBatchConversion() {
        // 准备测试数据
        List<Category> categories = Arrays.asList(
                Category.builder()
                        .categoryId(1L)
                        .categoryCode("A")
                        .categoryName("马克思主义")
                        .categoryLevel(1)
                        .isActive(true)
                        .build(),
                Category.builder()
                        .categoryId(2L)
                        .categoryCode("B")
                        .categoryName("哲学")
                        .categoryLevel(1)
                        .isActive(true)
                        .build()
        );

        // 执行批量转换
        List<CategorySimpleResponse> responses = CategoryConverter.toCategorySimpleResponseList(categories);

        // 验证转换结果
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        CategorySimpleResponse first = responses.get(0);
        assertEquals(1L, first.getCategoryId());
        assertEquals("A", first.getCategoryCode());
        assertEquals("马克思主义", first.getCategoryName());
        
        CategorySimpleResponse second = responses.get(1);
        assertEquals(2L, second.getCategoryId());
        assertEquals("B", second.getCategoryCode());
        assertEquals("哲学", second.getCategoryName());
    }

    @Test
    public void testActiveStatusText() {
        // 测试启用状态文本转换
        assertEquals("启用", CategoryConverter.getActiveStatusText(true));
        assertEquals("禁用", CategoryConverter.getActiveStatusText(false));
        assertEquals("未知", CategoryConverter.getActiveStatusText(null));
    }

    @Test
    public void testSetCategoryStatistics() {
        // 准备测试数据
        CategoryInfoResponse response = new CategoryInfoResponse();
        
        // 设置统计信息
        CategoryConverter.setCategoryStatistics(response, 3, 15);
        
        // 验证结果
        assertEquals(3, response.getChildCount());
        assertEquals(15, response.getBookCount());
        assertTrue(response.getHasChildren());
        assertFalse(response.getIsLeaf());
        
        // 测试无子分类情况
        CategoryConverter.setCategoryStatistics(response, 0, 5);
        assertEquals(0, response.getChildCount());
        assertEquals(5, response.getBookCount());
        assertFalse(response.getHasChildren());
        assertTrue(response.getIsLeaf());
    }
} 