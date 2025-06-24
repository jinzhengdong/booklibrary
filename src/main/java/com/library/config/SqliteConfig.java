package com.library.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SQLite数据库配置类
 * 处理SQLite与Java时间类型的兼容性问题
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Configuration
public class SqliteConfig {

    /**
     * SQLite LocalDateTime类型处理器
     * 解决SQLite时间戳格式与Java LocalDateTime的兼容性问题
     */
    @MappedTypes(LocalDateTime.class)
    public static class SqliteLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {
        
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
            ps.setString(i, parameter.format(FORMATTER));
        }

        @Override
        public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
            String value = rs.getString(columnName);
            return parseDateTime(value);
        }

        @Override
        public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
            String value = rs.getString(columnIndex);
            return parseDateTime(value);
        }

        @Override
        public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
            String value = cs.getString(columnIndex);
            return parseDateTime(value);
        }
        
        /**
         * 解析日期时间字符串，支持多种格式
         */
        private LocalDateTime parseDateTime(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            
            try {
                // 首先尝试标准格式 yyyy-MM-dd HH:mm:ss
                return LocalDateTime.parse(value, FORMATTER);
            } catch (Exception e1) {
                try {
                    // 尝试ISO格式，但可能包含纳秒，需要截断
                    if (value.contains("T")) {
                        // 处理纳秒精度过高的问题
                        String processedValue = value;
                        if (value.contains(".")) {
                            // 截断纳秒到毫秒精度（最多3位小数）
                            int dotIndex = value.indexOf(".");
                            int endIndex = value.length();
                            if (endIndex - dotIndex > 4) { // .xxx 格式，保留3位小数
                                processedValue = value.substring(0, dotIndex + 4);
                            }
                        }
                        return LocalDateTime.parse(processedValue, ISO_FORMATTER);
                    }
                } catch (Exception e2) {
                    // 如果都失败了，记录错误并返回null
                    System.err.println("无法解析日期时间格式: " + value + ", 错误: " + e2.getMessage());
                }
            }
            return null;
        }
    }
} 