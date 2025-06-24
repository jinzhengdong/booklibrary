package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 图书信息Mapper接口
 * 提供图书数据访问操作
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 根据ISBN查询图书
     * 
     * @param isbn ISBN编号
     * @return 图书信息
     */
    @Select("SELECT * FROM books WHERE isbn = #{isbn} AND is_active = 1")
    Book findByIsbn(@Param("isbn") String isbn);

    /**
     * 根据分类ID查询图书列表
     * 
     * @param categoryId 分类ID
     * @return 图书列表
     */
    @Select("SELECT * FROM books WHERE category_id = #{categoryId} AND is_active = 1 ORDER BY title")
    List<Book> findByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 分页查询图书信息
     * 
     * @param page 分页参数
     * @param categoryId 分类ID（可选）
     * @param keyword 关键词搜索（书名、作者、ISBN）
     * @param available 是否只查询可借图书
     * @return 分页图书列表
     */
    @Select("<script>" +
            "SELECT b.*, c.category_name FROM books b " +
            "LEFT JOIN categories c ON b.category_id = c.category_id " +
            "WHERE b.is_active = 1 " +
            "<if test='categoryId != null'>" +
            "AND b.category_id = #{categoryId} " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (b.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR b.author LIKE CONCAT('%', #{keyword}, '%') " +
            "OR b.isbn LIKE CONCAT('%', #{keyword}, '%') " +
            "OR b.publisher LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='available != null and available == true'>" +
            "AND b.available_copies > 0 " +
            "</if>" +
            "ORDER BY b.created_time DESC" +
            "</script>")
    IPage<Book> findBooksWithConditions(Page<Book> page,
                                       @Param("categoryId") Long categoryId,
                                       @Param("keyword") String keyword,
                                       @Param("available") Boolean available);

    /**
     * 根据作者查询图书
     * 
     * @param author 作者名称
     * @return 图书列表
     */
    @Select("SELECT * FROM books WHERE author LIKE CONCAT('%', #{author}, '%') AND is_active = 1 ORDER BY title")
    List<Book> findByAuthor(@Param("author") String author);

    /**
     * 根据出版社查询图书
     * 
     * @param publisher 出版社名称
     * @return 图书列表
     */
    @Select("SELECT * FROM books WHERE publisher LIKE CONCAT('%', #{publisher}, '%') AND is_active = 1 ORDER BY title")
    List<Book> findByPublisher(@Param("publisher") String publisher);

    /**
     * 查询可借阅的图书
     * 
     * @return 可借阅图书列表
     */
    @Select("SELECT * FROM books WHERE available_copies > 0 AND is_active = 1 ORDER BY title")
    List<Book> findAvailableBooks();

    /**
     * 查询库存不足的图书
     * 
     * @param threshold 库存阈值
     * @return 库存不足的图书列表
     */
    @Select("SELECT * FROM books WHERE available_copies &lt;= #{threshold} AND is_active = 1 ORDER BY available_copies")
    List<Book> findLowStockBooks(@Param("threshold") Integer threshold);

    /**
     * 查询热门图书（按借阅次数排序）
     * 
     * @param limit 返回数量限制
     * @return 热门图书列表
     */
    @Select("SELECT b.*, COUNT(br.book_id) as borrow_count FROM books b " +
            "LEFT JOIN borrow_records br ON b.book_id = br.book_id " +
            "WHERE b.is_active = 1 " +
            "GROUP BY b.book_id " +
            "ORDER BY borrow_count DESC " +
            "LIMIT #{limit}")
    List<Book> findPopularBooks(@Param("limit") Integer limit);

    /**
     * 查询新增图书
     * 
     * @param days 最近天数
     * @return 新增图书列表
     */
    @Select("SELECT * FROM books WHERE is_active = 1 " +
            "AND created_time &gt;= DATE('now', '-' || #{days} || ' days') " +
            "ORDER BY created_time DESC")
    List<Book> findRecentBooks(@Param("days") Integer days);

    /**
     * 根据位置查询图书
     * 
     * @param location 图书位置
     * @return 图书列表
     */
    @Select("SELECT * FROM books WHERE location LIKE CONCAT('%', #{location}, '%') AND is_active = 1 ORDER BY location, title")
    List<Book> findByLocation(@Param("location") String location);

    /**
     * 更新图书库存
     * 
     * @param bookId 图书ID
     * @param availableCopies 可借册数
     * @param borrowedCopies 已借册数
     * @return 更新行数
     */
    @Update("UPDATE books SET available_copies = #{availableCopies}, borrowed_copies = #{borrowedCopies}, " +
            "updated_time = CURRENT_TIMESTAMP WHERE book_id = #{bookId}")
    int updateBookStock(@Param("bookId") Long bookId,
                       @Param("availableCopies") Integer availableCopies,
                       @Param("borrowedCopies") Integer borrowedCopies);

    /**
     * 增加图书可借册数（归还时调用）
     * 
     * @param bookId 图书ID
     * @return 更新行数
     */
    @Update("UPDATE books SET available_copies = available_copies + 1, borrowed_copies = borrowed_copies - 1, " +
            "updated_time = CURRENT_TIMESTAMP WHERE book_id = #{bookId}")
    int increaseAvailableCopies(@Param("bookId") Long bookId);

    /**
     * 减少图书可借册数（借阅时调用）
     * 
     * @param bookId 图书ID
     * @return 更新行数
     */
    @Update("UPDATE books SET available_copies = available_copies - 1, borrowed_copies = borrowed_copies + 1, " +
            "updated_time = CURRENT_TIMESTAMP WHERE book_id = #{bookId} AND available_copies > 0")
    int decreaseAvailableCopies(@Param("bookId") Long bookId);

    /**
     * 统计图书总数
     * 
     * @return 图书总数
     */
    @Select("SELECT COUNT(*) FROM books WHERE is_active = 1")
    int countTotalBooks();

    /**
     * 统计可借图书总数
     * 
     * @return 可借图书总数
     */
    @Select("SELECT COUNT(*) FROM books WHERE available_copies > 0 AND is_active = 1")
    int countAvailableBooks();

    /**
     * 统计各分类图书数量
     * 
     * @return 统计结果
     */
    @Select("SELECT c.category_name, COUNT(b.book_id) as book_count FROM categories c " +
            "LEFT JOIN books b ON c.category_id = b.category_id AND b.is_active = 1 " +
            "WHERE c.is_active = 1 " +
            "GROUP BY c.category_id, c.category_name " +
            "ORDER BY book_count DESC")
    List<Book> countBooksByCategory();

    /**
     * 查询图书详细信息（包含分类信息）
     * 
     * @param bookId 图书ID
     * @return 图书详细信息
     */
    @Select("SELECT b.*, c.category_name, c.category_code FROM books b " +
            "LEFT JOIN categories c ON b.category_id = c.category_id " +
            "WHERE b.book_id = #{bookId} AND b.is_active = 1")
    Book findBookWithCategory(@Param("bookId") Long bookId);

    /**
     * 检查图书是否可借
     * 
     * @param bookId 图书ID
     * @return 是否可借
     */
    @Select("SELECT CASE WHEN available_copies > 0 THEN 1 ELSE 0 END FROM books " +
            "WHERE book_id = #{bookId} AND is_active = 1")
    Boolean isBookAvailable(@Param("bookId") Long bookId);

    /**
     * 查询相似图书（同作者或同分类）
     * 
     * @param bookId 图书ID
     * @param limit 返回数量限制
     * @return 相似图书列表
     */
    @Select("SELECT DISTINCT b2.* FROM books b1 " +
            "JOIN books b2 ON (b1.author = b2.author OR b1.category_id = b2.category_id) " +
            "WHERE b1.book_id = #{bookId} AND b2.book_id != #{bookId} " +
            "AND b1.is_active = 1 AND b2.is_active = 1 " +
            "ORDER BY CASE WHEN b1.author = b2.author THEN 0 ELSE 1 END, b2.title " +
            "LIMIT #{limit}")
    List<Book> findSimilarBooks(@Param("bookId") Long bookId, @Param("limit") Integer limit);

    /**
     * 批量更新图书状态
     * 
     * @param bookIds 图书ID列表
     * @param isActive 是否启用
     * @return 更新行数
     */
    @Update("<script>" +
            "UPDATE books SET is_active = #{isActive}, updated_time = CURRENT_TIMESTAMP " +
            "WHERE book_id IN " +
            "<foreach collection='bookIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateActiveStatus(@Param("bookIds") List<Long> bookIds, @Param("isActive") Boolean isActive);

    /**
     * 查询库存统计信息
     * 
     * @return 库存统计
     */
    @Select("SELECT " +
            "COUNT(*) as total_books, " +
            "SUM(total_copies) as total_copies, " +
            "SUM(available_copies) as available_copies, " +
            "SUM(borrowed_copies) as borrowed_copies " +
            "FROM books WHERE is_active = 1")
    Book getStockStatistics();
} 