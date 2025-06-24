package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约记录Mapper接口
 * 提供预约记录数据访问操作
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 根据用户ID查询预约记录
     * 
     * @param userId 用户ID
     * @return 预约记录列表
     */
    @Select("SELECT r.*, b.title, b.author, b.isbn " +
            "FROM reservations r " +
            "LEFT JOIN books b ON r.book_id = b.book_id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.reservation_date DESC")
    List<Reservation> findByUserId(@Param("userId") Long userId);

    /**
     * 根据图书ID查询预约记录
     * 
     * @param bookId 图书ID
     * @return 预约记录列表
     */
    @Select("SELECT r.*, u.real_name, u.username " +
            "FROM reservations r " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "WHERE r.book_id = #{bookId} " +
            "ORDER BY r.queue_position ASC")
    List<Reservation> findByBookId(@Param("bookId") Long bookId);

    /**
     * 查询用户当前有效的预约
     * 
     * @param userId 用户ID
     * @return 当前有效预约列表
     */
    @Select("SELECT r.*, b.title, b.author, b.isbn " +
            "FROM reservations r " +
            "LEFT JOIN books b ON r.book_id = b.book_id " +
            "WHERE r.user_id = #{userId} AND r.status IN ('waiting', 'notified') " +
            "ORDER BY r.reservation_date DESC")
    List<Reservation> findActiveReservationsByUserId(@Param("userId") Long userId);

    /**
     * 查询图书的等待队列
     * 
     * @param bookId 图书ID
     * @return 等待队列列表
     */
    @Select("SELECT r.*, u.real_name, u.username " +
            "FROM reservations r " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "WHERE r.book_id = #{bookId} AND r.status = 'waiting' " +
            "ORDER BY r.queue_position ASC")
    List<Reservation> findWaitingQueueByBookId(@Param("bookId") Long bookId);

    /**
     * 查询需要通知的预约
     * 
     * @return 需要通知的预约列表
     */
    @Select("SELECT r.*, b.title, b.author, u.real_name, u.phone, u.email " +
            "FROM reservations r " +
            "LEFT JOIN books b ON r.book_id = b.book_id " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "WHERE r.status = 'notified' AND r.expiry_date &gt;= DATE('now') " +
            "ORDER BY r.notification_date ASC")
    List<Reservation> findNotifiedReservations();

    /**
     * 查询过期的预约
     * 
     * @return 过期预约列表
     */
    @Select("SELECT r.*, b.title, b.author, u.real_name " +
            "FROM reservations r " +
            "LEFT JOIN books b ON r.book_id = b.book_id " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "WHERE r.status = 'notified' AND r.expiry_date &lt; DATE('now') " +
            "ORDER BY r.expiry_date ASC")
    List<Reservation> findExpiredReservations();

    /**
     * 分页查询预约记录
     * 
     * @param page 分页参数
     * @param userId 用户ID（可选）
     * @param status 预约状态（可选）
     * @param keyword 关键词搜索（用户名、图书名）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页预约记录列表
     */
    @Select("<script>" +
            "SELECT r.*, b.title, b.author, b.isbn, u.real_name, u.username " +
            "FROM reservations r " +
            "LEFT JOIN books b ON r.book_id = b.book_id " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "WHERE 1=1 " +
            "<if test='userId != null'>" +
            "AND r.user_id = #{userId} " +
            "</if>" +
            "<if test='status != null and status != \"\"'>" +
            "AND r.status = #{status} " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (u.real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR b.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR b.author LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='startDate != null'>" +
            "AND r.reservation_date &gt;= #{startDate} " +
            "</if>" +
            "<if test='endDate != null'>" +
            "AND r.reservation_date &lt;= #{endDate} " +
            "</if>" +
            "ORDER BY r.reservation_date DESC" +
            "</script>")
    IPage<Reservation> findReservationsWithConditions(Page<Reservation> page,
                                                     @Param("userId") Long userId,
                                                     @Param("status") String status,
                                                     @Param("keyword") String keyword,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 统计用户预约次数
     * 
     * @param userId 用户ID
     * @return 预约次数
     */
    @Select("SELECT COUNT(*) FROM reservations WHERE user_id = #{userId}")
    int countReservationsByUserId(@Param("userId") Long userId);

    /**
     * 统计用户当前有效预约数量
     * 
     * @param userId 用户ID
     * @return 当前有效预约数量
     */
    @Select("SELECT COUNT(*) FROM reservations WHERE user_id = #{userId} AND status IN ('waiting', 'notified')")
    int countActiveReservationsByUserId(@Param("userId") Long userId);

    /**
     * 统计图书预约次数
     * 
     * @param bookId 图书ID
     * @return 预约次数
     */
    @Select("SELECT COUNT(*) FROM reservations WHERE book_id = #{bookId}")
    int countReservationsByBookId(@Param("bookId") Long bookId);

    /**
     * 获取图书当前队列长度
     * 
     * @param bookId 图书ID
     * @return 队列长度
     */
    @Select("SELECT COUNT(*) FROM reservations WHERE book_id = #{bookId} AND status = 'waiting'")
    int getQueueLengthByBookId(@Param("bookId") Long bookId);

    /**
     * 获取下一个队列位置
     * 
     * @param bookId 图书ID
     * @return 下一个队列位置
     */
    @Select("SELECT COALESCE(MAX(queue_position), 0) + 1 FROM reservations WHERE book_id = #{bookId}")
    int getNextQueuePosition(@Param("bookId") Long bookId);

    /**
     * 查询队列中的第一个预约
     * 
     * @param bookId 图书ID
     * @return 队列中的第一个预约
     */
    @Select("SELECT r.*, u.real_name, u.phone, u.email " +
            "FROM reservations r " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "WHERE r.book_id = #{bookId} AND r.status = 'waiting' " +
            "ORDER BY r.queue_position ASC " +
            "LIMIT 1")
    Reservation findFirstInQueue(@Param("bookId") Long bookId);

    /**
     * 更新预约状态为已通知
     * 
     * @param reservationId 预约ID
     * @param notificationDate 通知日期
     * @param expiryDate 过期日期
     * @return 更新行数
     */
    @Update("UPDATE reservations SET status = 'notified', notification_date = #{notificationDate}, " +
            "expiry_date = #{expiryDate}, updated_time = CURRENT_TIMESTAMP " +
            "WHERE reservation_id = #{reservationId}")
    int updateToNotified(@Param("reservationId") Long reservationId,
                        @Param("notificationDate") LocalDate notificationDate,
                        @Param("expiryDate") LocalDate expiryDate);

    /**
     * 更新预约状态为已完成
     * 
     * @param reservationId 预约ID
     * @return 更新行数
     */
    @Update("UPDATE reservations SET status = 'fulfilled', updated_time = CURRENT_TIMESTAMP " +
            "WHERE reservation_id = #{reservationId}")
    int updateToFulfilled(@Param("reservationId") Long reservationId);

    /**
     * 取消预约
     * 
     * @param reservationId 预约ID
     * @return 更新行数
     */
    @Update("UPDATE reservations SET status = 'cancelled', updated_time = CURRENT_TIMESTAMP " +
            "WHERE reservation_id = #{reservationId}")
    int cancelReservation(@Param("reservationId") Long reservationId);

    /**
     * 批量更新过期预约状态
     * 
     * @return 更新行数
     */
    @Update("UPDATE reservations SET status = 'expired', updated_time = CURRENT_TIMESTAMP " +
            "WHERE status = 'notified' AND expiry_date &lt; DATE('now')")
    int batchUpdateExpiredStatus();

    /**
     * 调整队列位置（删除预约后）
     * 
     * @param bookId 图书ID
     * @param queuePosition 删除的队列位置
     * @return 更新行数
     */
    @Update("UPDATE reservations SET queue_position = queue_position - 1, updated_time = CURRENT_TIMESTAMP " +
            "WHERE book_id = #{bookId} AND queue_position > #{queuePosition} AND status = 'waiting'")
    int adjustQueuePosition(@Param("bookId") Long bookId, @Param("queuePosition") Integer queuePosition);

    /**
     * 检查用户是否已预约该图书
     * 
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 预约数量
     */
    @Select("SELECT COUNT(*) FROM reservations " +
            "WHERE user_id = #{userId} AND book_id = #{bookId} AND status IN ('waiting', 'notified')")
    int checkUserBookReservation(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * 查询热门预约图书统计
     * 
     * @param limit 返回数量限制
     * @param days 统计天数（可选）
     * @return 热门预约图书统计
     */
    @Select("<script>" +
            "SELECT b.title, b.author, COUNT(r.book_id) as reservation_count " +
            "FROM reservations r " +
            "LEFT JOIN books b ON r.book_id = b.book_id " +
            "WHERE 1=1 " +
            "<if test='days != null'>" +
            "AND r.reservation_date &gt;= DATE('now', '-' || #{days} || ' days') " +
            "</if>" +
            "GROUP BY r.book_id, b.title, b.author " +
            "ORDER BY reservation_count DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<Reservation> findPopularReservationStatistics(@Param("limit") Integer limit, @Param("days") Integer days);

    /**
     * 查询预约统计报表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预约统计报表
     */
    @Select("SELECT " +
            "DATE(reservation_date) as reservation_date, " +
            "COUNT(*) as daily_reservation_count, " +
            "COUNT(CASE WHEN status = 'fulfilled' THEN 1 END) as daily_fulfilled_count " +
            "FROM reservations " +
            "WHERE reservation_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(reservation_date) " +
            "ORDER BY reservation_date")
    List<Reservation> findReservationStatistics(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 查询用户预约历史
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 用户预约历史
     */
    @Select("SELECT r.*, b.title, b.author, b.isbn, b.publisher " +
            "FROM reservations r " +
            "LEFT JOIN books b ON r.book_id = b.book_id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.reservation_date DESC " +
            "LIMIT #{limit}")
    List<Reservation> findUserReservationHistory(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 查询需要清理的预约（已过期超过指定天数）
     * 
     * @param days 过期天数
     * @return 需要清理的预约列表
     */
    @Select("SELECT * FROM reservations " +
            "WHERE status = 'expired' AND expiry_date &lt; DATE('now', '-' || #{days} || ' days') " +
            "ORDER BY expiry_date ASC")
    List<Reservation> findReservationsToCleanup(@Param("days") Integer days);

    /**
     * 获取预约详细信息（包含用户和图书信息）
     * 
     * @param reservationId 预约ID
     * @return 预约详细信息
     */
    @Select("SELECT r.*, b.title, b.author, b.isbn, b.available_copies, " +
            "u.real_name, u.username, u.phone, u.email " +
            "FROM reservations r " +
            "LEFT JOIN books b ON r.book_id = b.book_id " +
            "LEFT JOIN users u ON r.user_id = u.user_id " +
            "WHERE r.reservation_id = #{reservationId}")
    Reservation findReservationDetail(@Param("reservationId") Long reservationId);
} 