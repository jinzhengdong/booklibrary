package com.library.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 对应数据表：system_config
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("system_config")
public class SystemConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID（主键，自增）
     */
    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    /**
     * 配置键（唯一）
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置类型（string/integer/decimal/boolean）
     */
    @TableField("config_type")
    private String configType;

    /**
     * 配置分组
     */
    @TableField("config_group")
    private String configGroup;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    // 配置类型枚举
    public static class ConfigType {
        public static final String STRING = "string";
        public static final String INTEGER = "integer";
        public static final String DECIMAL = "decimal";
        public static final String BOOLEAN = "boolean";
    }

    // 配置分组枚举
    public static class ConfigGroup {
        public static final String SYSTEM = "system";
        public static final String BORROW = "borrow";
        public static final String FINE = "fine";
        public static final String RESERVATION = "reservation";
        public static final String NOTIFICATION = "notification";
        public static final String BACKUP = "backup";
    }
} 