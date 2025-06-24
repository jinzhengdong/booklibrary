package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 图书分类Mapper接口
 * 提供图书分类数据访问操作
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据分类代码查询分类
     * 
     * @param categoryCode 分类代码
     * @return 分类信息
     */
    @Select("SELECT * FROM categories WHERE category_code = #{categoryCode} AND is_active = 1")
    Category findByCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     * 查询所有顶级分类
     * 
     * @return 顶级分类列表
     */
    @Select("SELECT * FROM categories WHERE parent_category_id IS NULL AND is_active = 1 ORDER BY sort_order, category_code")
    List<Category> findTopLevelCategories();

    /**
     * 根据父分类ID查询子分类
     * 
     * @param parentCategoryId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM categories WHERE parent_category_id = #{parentCategoryId} AND is_active = 1 ORDER BY sort_order, category_code")
    List<Category> findByParentCategoryId(@Param("parentCategoryId") Long parentCategoryId);

    /**
     * 根据分类层级查询分类
     * 
     * @param categoryLevel 分类层级
     * @return 分类列表
     */
    @Select("SELECT * FROM categories WHERE category_level = #{categoryLevel} AND is_active = 1 ORDER BY sort_order, category_code")
    List<Category> findByCategoryLevel(@Param("categoryLevel") Integer categoryLevel);

    /**
     * 查询分类树结构（递归查询）
     * 
     * @return 完整的分类树
     */
    @Select("WITH RECURSIVE category_tree AS (" +
            "  SELECT *, 0 as depth FROM categories WHERE parent_category_id IS NULL AND is_active = 1 " +
            "  UNION ALL " +
            "  SELECT c.*, ct.depth + 1 FROM categories c " +
            "  INNER JOIN category_tree ct ON c.parent_category_id = ct.category_id " +
            "  WHERE c.is_active = 1" +
            ") SELECT * FROM category_tree ORDER BY depth, sort_order, category_code")
    List<Category> findCategoryTree();

    /**
     * 根据分类名称模糊查询
     * 
     * @param categoryName 分类名称关键词
     * @return 匹配的分类列表
     */
    @Select("SELECT * FROM categories WHERE category_name LIKE CONCAT('%', #{categoryName}, '%') AND is_active = 1 ORDER BY category_level, sort_order")
    List<Category> findByCategoryNameLike(@Param("categoryName") String categoryName);

    /**
     * 统计某分类下的图书数量
     * 
     * @param categoryId 分类ID
     * @return 图书数量
     */
    @Select("SELECT COUNT(*) FROM books WHERE category_id = #{categoryId} AND is_active = 1")
    int countBooksByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 查询有图书的分类列表
     * 
     * @return 有图书的分类列表
     */
    @Select("SELECT DISTINCT c.* FROM categories c " +
            "INNER JOIN books b ON c.category_id = b.category_id " +
            "WHERE c.is_active = 1 AND b.is_active = 1 " +
            "ORDER BY c.category_level, c.sort_order")
    List<Category> findCategoriesWithBooks();

    /**
     * 查询某分类的所有子分类ID（包括自身）
     * 
     * @param categoryId 分类ID
     * @return 分类ID列表
     */
    @Select("WITH RECURSIVE category_children AS (" +
            "  SELECT category_id FROM categories WHERE category_id = #{categoryId} AND is_active = 1 " +
            "  UNION ALL " +
            "  SELECT c.category_id FROM categories c " +
            "  INNER JOIN category_children cc ON c.parent_category_id = cc.category_id " +
            "  WHERE c.is_active = 1" +
            ") SELECT category_id FROM category_children")
    List<Long> findCategoryIdWithChildren(@Param("categoryId") Long categoryId);

    /**
     * 获取分类的完整路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径（从根到当前分类）
     */
    @Select("WITH RECURSIVE category_path AS (" +
            "  SELECT category_id, category_name, parent_category_id, 0 as level " +
            "  FROM categories WHERE category_id = #{categoryId} AND is_active = 1 " +
            "  UNION ALL " +
            "  SELECT c.category_id, c.category_name, c.parent_category_id, cp.level + 1 " +
            "  FROM categories c " +
            "  INNER JOIN category_path cp ON c.category_id = cp.parent_category_id " +
            "  WHERE c.is_active = 1" +
            ") SELECT * FROM category_path ORDER BY level DESC")
    List<Category> findCategoryPath(@Param("categoryId") Long categoryId);

    /**
     * 更新分类排序
     * 
     * @param categoryId 分类ID
     * @param sortOrder 新的排序值
     * @return 更新行数
     */
    @Update("UPDATE categories SET sort_order = #{sortOrder}, updated_time = CURRENT_TIMESTAMP WHERE category_id = #{categoryId}")
    int updateSortOrder(@Param("categoryId") Long categoryId, @Param("sortOrder") Integer sortOrder);

    /**
     * 启用或禁用分类
     * 
     * @param categoryId 分类ID
     * @param isActive 是否启用
     * @return 更新行数
     */
    @Update("UPDATE categories SET is_active = #{isActive}, updated_time = CURRENT_TIMESTAMP WHERE category_id = #{categoryId}")
    int updateActiveStatus(@Param("categoryId") Long categoryId, @Param("isActive") Boolean isActive);

    /**
     * 查询最大排序值
     * 
     * @param parentCategoryId 父分类ID
     * @return 最大排序值
     */
    @Select("SELECT COALESCE(MAX(sort_order), 0) FROM categories WHERE " +
            "parent_category_id = #{parentCategoryId} OR (parent_category_id IS NULL AND #{parentCategoryId} IS NULL)")
    Integer findMaxSortOrder(@Param("parentCategoryId") Long parentCategoryId);

    /**
     * 检查分类代码是否已存在
     * 
     * @param categoryCode 分类代码
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return 存在的数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM categories WHERE category_code = #{categoryCode} " +
            "<if test='excludeId != null'>" +
            "AND category_id != #{excludeId} " +
            "</if>" +
            "</script>")
    int countByCategoryCode(@Param("categoryCode") String categoryCode, @Param("excludeId") Long excludeId);

    /**
     * 统计各层级分类的数量
     * 
     * @return 统计结果
     */
    @Select("SELECT category_level, COUNT(*) as count FROM categories WHERE is_active = 1 GROUP BY category_level ORDER BY category_level")
    List<Category> countByCategoryLevel();
} 