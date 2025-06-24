package com.library.service;

import com.library.dto.request.CategoryCreateRequest;
import com.library.dto.request.CategoryQueryRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.CategoryInfoResponse;
import com.library.dto.response.CategoryListResponse;
import com.library.dto.response.CategorySimpleResponse;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import com.library.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 分类服务测试类
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .categoryId(1L)
                .categoryCode("CS001")
                .categoryName("计算机科学")
                .parentCategoryId(null)
                .categoryLevel(1)
                .sortOrder(1)
                .description("计算机科学类图书")
                .isActive(true)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        createRequest = new CategoryCreateRequest();
        createRequest.setCategoryCode("CS002");
        createRequest.setCategoryName("软件工程");
        createRequest.setParentCategoryId(1L);
        createRequest.setDescription("软件工程类图书");
    }

    @Test
    void testCreateCategory_Success() {
        // Given
        when(categoryMapper.findByCategoryCode(createRequest.getCategoryCode())).thenReturn(null);
        when(categoryMapper.selectById(createRequest.getParentCategoryId())).thenReturn(testCategory);
        when(categoryMapper.findMaxSortOrder(createRequest.getParentCategoryId())).thenReturn(1);
        when(categoryMapper.insert(any(Category.class))).thenReturn(1);

        // When
        ApiResponse<CategoryInfoResponse> response = categoryService.createCategory(createRequest);

        // Then
        assertTrue(response.getSuccess());
        assertEquals("分类创建成功", response.getMessage());
        verify(categoryMapper, times(1)).insert(any(Category.class));
    }

    @Test
    void testCreateCategory_DuplicateCode() {
        // Given
        when(categoryMapper.findByCategoryCode(createRequest.getCategoryCode())).thenReturn(testCategory);

        // When
        ApiResponse<CategoryInfoResponse> response = categoryService.createCategory(createRequest);

        // Then
        assertFalse(response.getSuccess());
        assertEquals("分类代码已存在", response.getMessage());
        verify(categoryMapper, never()).insert(any(Category.class));
    }

    @Test
    void testGetCategoryById_Success() {
        // Given
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);
        when(categoryMapper.findByParentCategoryId(1L)).thenReturn(Arrays.asList());
        when(categoryMapper.countBooksByCategoryId(1L)).thenReturn(5);

        // When
        ApiResponse<CategoryInfoResponse> response = categoryService.getCategoryById(1L);

        // Then
        assertTrue(response.getSuccess());
        assertNotNull(response.getData());
        assertEquals("CS001", response.getData().getCategoryCode());
        assertEquals("计算机科学", response.getData().getCategoryName());
    }

    @Test
    void testGetCategoryById_NotFound() {
        // Given
        when(categoryMapper.selectById(1L)).thenReturn(null);

        // When
        ApiResponse<CategoryInfoResponse> response = categoryService.getCategoryById(1L);

        // Then
        assertFalse(response.getSuccess());
        assertEquals("分类不存在", response.getMessage());
    }

    @Test
    void testGetTopLevelCategories_Success() {
        // Given
        List<Category> topCategories = Arrays.asList(testCategory);
        when(categoryMapper.findTopLevelCategories()).thenReturn(topCategories);

        // When
        ApiResponse<List<CategorySimpleResponse>> response = categoryService.getTopLevelCategories();

        // Then
        assertTrue(response.getSuccess());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals("CS001", response.getData().get(0).getCategoryCode());
    }

    @Test
    void testCheckCategoryCodeAvailable_Available() {
        // Given
        when(categoryMapper.countByCategoryCode("NEW001", null)).thenReturn(0);

        // When
        ApiResponse<Boolean> response = categoryService.checkCategoryCodeAvailable("NEW001", null);

        // Then
        assertTrue(response.getSuccess());
        assertTrue(response.getData());
    }

    @Test
    void testCheckCategoryCodeAvailable_NotAvailable() {
        // Given
        when(categoryMapper.countByCategoryCode("CS001", null)).thenReturn(1);

        // When
        ApiResponse<Boolean> response = categoryService.checkCategoryCodeAvailable("CS001", null);

        // Then
        assertTrue(response.getSuccess());
        assertFalse(response.getData());
    }

    @Test
    void testDeleteCategory_Success() {
        // Given
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);
        when(categoryMapper.findByParentCategoryId(1L)).thenReturn(Arrays.asList());
        when(categoryMapper.countBooksByCategoryId(1L)).thenReturn(0);
        when(categoryMapper.deleteById(1L)).thenReturn(1);

        // When
        ApiResponse<Void> response = categoryService.deleteCategory(1L);

        // Then
        assertTrue(response.getSuccess());
        assertEquals("分类删除成功", response.getMessage());
        verify(categoryMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCategory_HasChildren() {
        // Given
        Category childCategory = Category.builder()
                .categoryId(2L)
                .categoryCode("CS002")
                .categoryName("软件工程")
                .parentCategoryId(1L)
                .build();
        
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);
        when(categoryMapper.findByParentCategoryId(1L)).thenReturn(Arrays.asList(childCategory));

        // When
        ApiResponse<Void> response = categoryService.deleteCategory(1L);

        // Then
        assertFalse(response.getSuccess());
        assertEquals("该分类下还有子分类，无法删除", response.getMessage());
        verify(categoryMapper, never()).deleteById(1L);
    }

    @Test
    void testDeleteCategory_HasBooks() {
        // Given
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);
        when(categoryMapper.findByParentCategoryId(1L)).thenReturn(Arrays.asList());
        when(categoryMapper.countBooksByCategoryId(1L)).thenReturn(5);

        // When
        ApiResponse<Void> response = categoryService.deleteCategory(1L);

        // Then
        assertFalse(response.getSuccess());
        assertEquals("该分类下还有图书，无法删除", response.getMessage());
        verify(categoryMapper, never()).deleteById(1L);
    }

    @Test
    void testValidateCategory_Valid() {
        // Given
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);

        // When
        Category result = categoryService.validateCategory(1L);

        // Then
        assertNotNull(result);
        assertEquals(testCategory.getCategoryId(), result.getCategoryId());
        assertEquals(testCategory.getCategoryCode(), result.getCategoryCode());
    }

    @Test
    void testValidateCategory_NotFound() {
        // Given
        when(categoryMapper.selectById(1L)).thenReturn(null);

        // When
        Category result = categoryService.validateCategory(1L);

        // Then
        assertNull(result);
    }

    @Test
    void testValidateCategory_Inactive() {
        // Given
        testCategory.setIsActive(false);
        when(categoryMapper.selectById(1L)).thenReturn(testCategory);

        // When
        Category result = categoryService.validateCategory(1L);

        // Then
        assertNull(result);
    }

    @Test
    void testGetMaxSortOrder_Success() {
        // Given
        when(categoryMapper.findMaxSortOrder(1L)).thenReturn(5);

        // When
        ApiResponse<Integer> response = categoryService.getMaxSortOrder(1L);

        // Then
        assertTrue(response.getSuccess());
        assertEquals(5, response.getData());
    }

    @Test
    void testGetMaxSortOrder_NoCategories() {
        // Given
        when(categoryMapper.findMaxSortOrder(1L)).thenReturn(null);

        // When
        ApiResponse<Integer> response = categoryService.getMaxSortOrder(1L);

        // Then
        assertTrue(response.getSuccess());
        assertEquals(0, response.getData());
    }
} 