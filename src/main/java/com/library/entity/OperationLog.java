package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 对应数据表：operation_logs
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("operation_logs")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID（主键，自增）
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 操作类型
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * 操作模块
     */
    @TableField("operation_module")
    private String operationModule;

    /**
     * 操作描述
     */
    @TableField("operation_description")
    private String operationDescription;

    /**
     * 目标对象ID
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 目标对象类型
     */
    @TableField("target_type")
    private String targetType;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 操作结果（success/failure）
     */
    @TableField("operation_result")
    private String operationResult;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    // 操作类型枚举
    public static class OperationType {
        public static final String LOGIN = "登录";
        public static final String LOGOUT = "登出";
        public static final String ADD = "新增";
        public static final String UPDATE = "修改";
        public static final String DELETE = "删除";
        public static final String QUERY = "查询";
        public static final String BORROW = "借阅";
        public static final String RETURN = "归还";
        public static final String RESERVE = "预约";
        public static final String RENEW = "续借";
        public static final String IMPORT = "导入";
        public static final String EXPORT = "导出";
        public static final String BACKUP = "备份";
    }

    // 操作模块枚举
    public static class OperationModule {
        public static final String USER_MANAGEMENT = "用户管理";
        public static final String BOOK_MANAGEMENT = "图书管理";
        public static final String BORROW_MANAGEMENT = "借阅管理";
        public static final String RESERVATION_MANAGEMENT = "预约管理";
        public static final String CATEGORY_MANAGEMENT = "分类管理";
        public static final String SYSTEM_MANAGEMENT = "系统管理";
        public static final String REPORT_MANAGEMENT = "报表管理";
    }

    // 操作结果枚举
    public static class OperationResult {
        public static final String SUCCESS = "success";
        public static final String FAILURE = "failure";
    }
} 