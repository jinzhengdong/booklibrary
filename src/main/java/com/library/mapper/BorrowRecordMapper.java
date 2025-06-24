package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

/**
 * 借阅记录Mapper接口
 * 提供借阅记录数据访问操作
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {

    /**
     * 根据用户ID查询借阅记录
     * 
     * @param userId 用户ID
     * @return 借阅记录列表
     */
    @Select("SELECT br.*, b.title, b.author, b.isbn, u.real_name, u.username " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.book_id " +
            "LEFT JOIN users u ON br.user_id = u.user_id " +
            "WHERE br.user_id = #{userId} " +
            "ORDER BY br.borrow_date DESC")
    List<BorrowRecord> findByUserId(@Param("userId") Long userId);

    /**
     * 根据图书ID查询借阅记录
     * 
     * @param bookId 图书ID
     * @return 借阅记录列表
     */
    @Select("SELECT br.*, b.title, b.author, u.real_name, u.username " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.book_id " +
            "LEFT JOIN users u ON br.user_id = u.user_id " +
            "WHERE br.book_id = #{bookId} " +
            "ORDER BY br.borrow_date DESC")
    List<BorrowRecord> findByBookId(@Param("bookId") Long bookId);

    /**
     * 查询用户当前借阅的图书
     * 
     * @param userId 用户ID
     * @return 当前借阅记录列表
     */
    @Select("SELECT br.*, b.title, b.author, b.isbn, b.location " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.book_id " +
            "WHERE br.user_id = #{userId} AND br.status = 'borrowed' " +
            "ORDER BY br.due_date ASC")
    List<BorrowRecord> findCurrentBorrowsByUserId(@Param("userId") Long userId);

    /**
     * 查询逾期的借阅记录
     * 
     * @return 逾期借阅记录列表
     */
    @Select("SELECT br.*, b.title, b.author, u.real_name, u.phone " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.book_id " +
            "LEFT JOIN users u ON br.user_id = u.user_id " +
            "WHERE br.status = 'borrowed' AND br.due_date &lt; DATE('now') " +
            "ORDER BY br.due_date ASC")
    List<BorrowRecord> findOverdueRecords();

    /**
     * 查询即将到期的借阅记录
     * 
     * @param days 提前天数
     * @return 即将到期的借阅记录列表
     */
    @Select("SELECT br.*, b.title, b.author, u.real_name, u.phone, u.email " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.book_id " +
            "LEFT JOIN users u ON br.user_id = u.user_id " +
            "WHERE br.status = 'borrowed' " +
            "AND br.due_date &lt;= DATE('now', '+' || #{days} || ' days') " +
            "AND br.due_date &gt;= DATE('now') " +
            "ORDER BY br.due_date ASC")
    List<BorrowRecord> findDueSoonRecords(@Param("days") Integer days);

    /**
     * 分页查询借阅记录
     * 
     * @param page 分页参数
     * @param userId 用户ID（可选）
     * @param status 借阅状态（可选）
     * @param keyword 关键词搜索（用户名、图书名）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页借阅记录列表
     */
    @Select("SELECT br.*, b.title, b.author, b.isbn, u.real_name, u.username " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.book_id " +
            "LEFT JOIN users u ON br.user_id = u.user_id " +
            "ORDER BY br.borrow_date DESC")
    IPage<BorrowRecord> findRecordsWithConditions(Page<BorrowRecord> page,
                                                 @Param("userId") Long userId,
                                                 @Param("status") String status,
                                                 @Param("keyword") String keyword,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * 统计用户借阅次数
     * 
     * @param userId 用户ID
     * @return 借阅次数
     */
    @Select("SELECT COUNT(*) FROM borrow_records WHERE user_id = #{userId}")
    int countBorrowsByUserId(@Param("userId") Long userId);

    /**
     * 统计图书借阅次数
     * 
     * @param bookId 图书ID
     * @return 借阅次数
     */
    @Select("SELECT COUNT(*) FROM borrow_records WHERE book_id = #{bookId}")
    int countBorrowsByBookId(@Param("bookId") Long bookId);

    /**
     * 统计用户当前借阅数量
     * 
     * @param userId 用户ID
     * @return 当前借阅数量
     */
    @Select("SELECT COUNT(*) FROM borrow_records WHERE user_id = #{userId} AND status = 'borrowed'")
    int countCurrentBorrowsByUserId(@Param("userId") Long userId);

    /**
     * 查询热门图书借阅统计
     * 
     * @param limit 返回数量限制
     * @param days 统计天数（可选）
     * @return 热门图书借阅统计
     */
    @Select("SELECT b.title, b.author, COUNT(br.book_id) as borrow_count " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.book_id " +
            "GROUP BY br.book_id, b.title, b.author " +
            "ORDER BY borrow_count DESC " +
            "LIMIT #{limit}")
    List<BorrowRecord> findPopularBooksStatistics(@Param("limit") Integer limit, @Param("days") Integer days);

    /**
     * 查询活跃用户借阅统计
     * 
     * @param limit 返回数量限制
     * @param days 统计天数（可选）
     * @return 活跃用户借阅统计
     */
    @Select("SELECT u.real_name, u.username, COUNT(br.user_id) as borrow_count " +
            "FROM borrow_records br " +
            "LEFT JOIN users u ON br.user_id = u.user_id " +
            "GROUP BY br.user_id, u.real_name, u.username " +
            "ORDER BY borrow_count DESC " +
            "LIMIT #{limit}")
    List<BorrowRecord> findActiveUsersStatistics(@Param("limit") Integer limit, @Param("days") Integer days);

    /**
     * 更新逾期信息
     * 
     * @param recordId 记录ID
     * @param overdueDays 逾期天数
     * @param overdueFee 逾期费用
     * @return 更新行数
     */
    @Update("UPDATE borrow_records SET overdue_days = #{overdueDays}, overdue_fee = #{overdueFee}, " +
            "status = 'overdue', updated_time = CURRENT_TIMESTAMP WHERE record_id = #{recordId}")
    int updateOverdueInfo(@Param("recordId") Long recordId,
                         @Param("overdueDays") Integer overdueDays,
                         @Param("overdueFee") Double overdueFee);

    /**
     * 归还图书
     * 
     * @param recordId 记录ID
     * @param actualReturnDate 实际归还日期
     * @return 更新行数
     */
    @Update("UPDATE borrow_records SET actual_return_date = #{actualReturnDate}, " +
            "status = 'returned', updated_time = CURRENT_TIMESTAMP WHERE record_id = #{recordId}")
    int returnBook(@Param("recordId") Long recordId, @Param("actualReturnDate") LocalDate actualReturnDate);

    /**
     * 续借图书
     * 
     * @param recordId 记录ID
     * @param newDueDate 新的到期日期
     * @return 更新行数
     */
    @Update("UPDATE borrow_records SET due_date = #{newDueDate}, renewal_times = renewal_times + 1, " +
            "updated_time = CURRENT_TIMESTAMP WHERE record_id = #{recordId}")
    int renewBook(@Param("recordId") Long recordId, @Param("newDueDate") LocalDate newDueDate);

    /**
     * 检查用户是否可以借阅图书
     * 
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 是否可以借阅
     */
    @Select("SELECT COUNT(*) FROM borrow_records " +
            "WHERE user_id = #{userId} AND book_id = #{bookId} AND status = 'borrowed'")
    int checkUserBookBorrow(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * 检查记录是否可以续借
     * 
     * @param recordId 记录ID
     * @return 是否可以续借
     */
    @Select("SELECT CASE WHEN renewal_times &lt; max_renewal_times AND status = 'borrowed' THEN 1 ELSE 0 END " +
            "FROM borrow_records WHERE record_id = #{recordId}")
    Boolean canRenewBook(@Param("recordId") Long recordId);

    /**
     * 查询借阅统计报表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 借阅统计报表
     */
    @Select("SELECT " +
            "DATE(borrow_date) as borrow_date, " +
            "COUNT(*) as daily_borrow_count, " +
            "COUNT(CASE WHEN status = 'returned' THEN 1 END) as daily_return_count " +
            "FROM borrow_records " +
            "WHERE borrow_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(borrow_date) " +
            "ORDER BY borrow_date")
    List<BorrowRecord> findBorrowStatistics(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    /**
     * 查询用户借阅历史（详细信息）
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 用户借阅历史
     */
    @Select("SELECT br.*, b.title, b.author, b.isbn, b.publisher " +
            "FROM borrow_records br " +
            "LEFT JOIN books b ON br.book_id = b.book_id " +
            "WHERE br.user_id = #{userId} " +
            "ORDER BY br.borrow_date DESC " +
            "LIMIT #{limit}")
    List<BorrowRecord> findUserBorrowHistory(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 批量更新逾期状态
     * 
     * @return 更新行数
     */
    @Update("UPDATE borrow_records SET " +
            "status = 'overdue', " +
            "overdue_days = JULIANDAY('now') - JULIANDAY(due_date), " +
            "updated_time = CURRENT_TIMESTAMP " +
            "WHERE status = 'borrowed' AND due_date &lt; DATE('now')")
    int batchUpdateOverdueStatus();
} 