package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Mapper接口
 * 提供操作日志数据访问操作
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 根据用户ID查询操作日志
     * 
     * @param userId 用户ID
     * @return 操作日志列表
     */
    @Select("SELECT ol.*, u.real_name, u.username " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "WHERE ol.user_id = #{userId} " +
            "ORDER BY ol.operation_time DESC")
    List<OperationLog> findByUserId(@Param("userId") Long userId);

    /**
     * 根据操作类型查询日志
     * 
     * @param operationType 操作类型
     * @return 操作日志列表
     */
    @Select("SELECT ol.*, u.real_name, u.username " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "WHERE ol.operation_type = #{operationType} " +
            "ORDER BY ol.operation_time DESC")
    List<OperationLog> findByOperationType(@Param("operationType") String operationType);

    /**
     * 根据操作结果查询日志
     * 
     * @param operationResult 操作结果
     * @return 操作日志列表
     */
    @Select("SELECT ol.*, u.real_name, u.username " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "WHERE ol.operation_result = #{operationResult} " +
            "ORDER BY ol.operation_time DESC")
    List<OperationLog> findByOperationResult(@Param("operationResult") String operationResult);

    /**
     * 分页查询操作日志
     * 
     * @param page 分页参数
     * @param userId 用户ID（可选）
     * @param operationType 操作类型（可选）
     * @param operationResult 操作结果（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param keyword 关键词搜索（用户名、IP地址）
     * @return 分页操作日志列表
     */
    @Select("<script>" +
            "SELECT ol.*, u.real_name, u.username " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "WHERE 1=1 " +
            "<if test='userId != null'>" +
            "AND ol.user_id = #{userId} " +
            "</if>" +
            "<if test='operationType != null and operationType != \"\"'>" +
            "AND ol.operation_type = #{operationType} " +
            "</if>" +
            "<if test='operationResult != null and operationResult != \"\"'>" +
            "AND ol.operation_result = #{operationResult} " +
            "</if>" +
            "<if test='startTime != null'>" +
            "AND ol.operation_time &gt;= #{startTime} " +
            "</if>" +
            "<if test='endTime != null'>" +
            "AND ol.operation_time &lt;= #{endTime} " +
            "</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (u.real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR ol.ip_address LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "ORDER BY ol.operation_time DESC" +
            "</script>")
    IPage<OperationLog> findLogsWithConditions(Page<OperationLog> page,
                                              @Param("userId") Long userId,
                                              @Param("operationType") String operationType,
                                              @Param("operationResult") String operationResult,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime,
                                              @Param("keyword") String keyword);

    /**
     * 查询最近的操作日志
     * 
     * @param limit 返回数量限制
     * @return 最近操作日志列表
     */
    @Select("SELECT ol.*, u.real_name, u.username " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "ORDER BY ol.operation_time DESC " +
            "LIMIT #{limit}")
    List<OperationLog> findRecentLogs(@Param("limit") Integer limit);

    /**
     * 查询失败的操作日志
     * 
     * @param hours 最近小时数
     * @return 失败操作日志列表
     */
    @Select("SELECT ol.*, u.real_name, u.username " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "WHERE ol.operation_result = 'failure' " +
            "AND ol.operation_time &gt;= DATETIME('now', '-' || #{hours} || ' hours') " +
            "ORDER BY ol.operation_time DESC")
    List<OperationLog> findFailureLogs(@Param("hours") Integer hours);

    /**
     * 根据IP地址查询操作日志
     * 
     * @param ipAddress IP地址
     * @return 操作日志列表
     */
    @Select("SELECT ol.*, u.real_name, u.username " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "WHERE ol.ip_address = #{ipAddress} " +
            "ORDER BY ol.operation_time DESC")
    List<OperationLog> findByIpAddress(@Param("ipAddress") String ipAddress);

    /**
     * 统计各操作类型的数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    @Select("SELECT operation_type, COUNT(*) as count " +
            "FROM operation_logs " +
            "WHERE operation_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY operation_type " +
            "ORDER BY count DESC")
    List<OperationLog> countByOperationType(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户操作活跃度
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 返回数量限制
     * @return 用户活跃度统计
     */
    @Select("SELECT u.real_name, u.username, COUNT(ol.user_id) as operation_count " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "WHERE ol.operation_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY ol.user_id, u.real_name, u.username " +
            "ORDER BY operation_count DESC " +
            "LIMIT #{limit}")
    List<OperationLog> findUserActivityStatistics(@Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime,
                                                  @Param("limit") Integer limit);

    /**
     * 查询操作时间统计（按小时）
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间统计结果
     */
    @Select("SELECT " +
            "strftime('%Y-%m-%d %H:00:00', operation_time) as hour_time, " +
            "COUNT(*) as operation_count " +
            "FROM operation_logs " +
            "WHERE operation_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY strftime('%Y-%m-%d %H:00:00', operation_time) " +
            "ORDER BY hour_time")
    List<OperationLog> findHourlyStatistics(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 查询操作时间统计（按天）
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间统计结果
     */
    @Select("SELECT " +
            "DATE(operation_time) as operation_date, " +
            "COUNT(*) as operation_count, " +
            "COUNT(CASE WHEN operation_result = 'success' THEN 1 END) as success_count, " +
            "COUNT(CASE WHEN operation_result = 'failure' THEN 1 END) as failure_count " +
            "FROM operation_logs " +
            "WHERE operation_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY DATE(operation_time) " +
            "ORDER BY operation_date")
    List<OperationLog> findDailyStatistics(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 查询异常IP访问记录
     * 
     * @param minCount 最小访问次数阈值
     * @param hours 时间范围（小时）
     * @return 异常IP列表
     */
    @Select("SELECT ip_address, COUNT(*) as access_count " +
            "FROM operation_logs " +
            "WHERE operation_time &gt;= DATETIME('now', '-' || #{hours} || ' hours') " +
            "GROUP BY ip_address " +
            "HAVING COUNT(*) &gt;= #{minCount} " +
            "ORDER BY access_count DESC")
    List<OperationLog> findAbnormalIpAccess(@Param("minCount") Integer minCount,
                                           @Param("hours") Integer hours);

    /**
     * 删除指定时间之前的日志
     * 
     * @param beforeTime 指定时间
     * @return 删除行数
     */
    @Delete("DELETE FROM operation_logs WHERE operation_time &lt; #{beforeTime}")
    int deleteLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 查询系统关键操作日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 关键操作日志列表
     */
    @Select("SELECT ol.*, u.real_name, u.username " +
            "FROM operation_logs ol " +
            "LEFT JOIN users u ON ol.user_id = u.user_id " +
            "WHERE ol.operation_type IN ('user_login', 'user_create', 'user_delete', 'book_create', 'book_delete', 'config_update') " +
            "AND ol.operation_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY ol.operation_time DESC")
    List<OperationLog> findCriticalOperations(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 查询用户登录历史
     * 
     * @param userId 用户ID
     * @param limit 返回数量限制
     * @return 登录历史列表
     */
    @Select("SELECT * FROM operation_logs " +
            "WHERE user_id = #{userId} AND operation_type = 'user_login' " +
            "ORDER BY operation_time DESC " +
            "LIMIT #{limit}")
    List<OperationLog> findUserLoginHistory(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 统计今日操作总数
     * 
     * @return 今日操作总数
     */
    @Select("SELECT COUNT(*) FROM operation_logs WHERE DATE(operation_time) = DATE('now')")
    int countTodayOperations();

    /**
     * 统计今日成功操作数
     * 
     * @return 今日成功操作数
     */
    @Select("SELECT COUNT(*) FROM operation_logs WHERE DATE(operation_time) = DATE('now') AND operation_result = 'success'")
    int countTodaySuccessOperations();

    /**
     * 统计今日失败操作数
     * 
     * @return 今日失败操作数
     */
    @Select("SELECT COUNT(*) FROM operation_logs WHERE DATE(operation_time) = DATE('now') AND operation_result = 'failure'")
    int countTodayFailureOperations();

    /**
     * 清理超过指定天数的日志
     * 
     * @param days 保留天数
     * @return 删除行数
     */
    @Delete("DELETE FROM operation_logs WHERE operation_time &lt; DATE('now', '-' || #{days} || ' days')")
    int cleanupOldLogs(@Param("days") Integer days);
} 