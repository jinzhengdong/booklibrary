package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 系统配置Mapper接口
 * 提供系统配置数据访问操作
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键查询配置
     * 
     * @param configKey 配置键
     * @return 系统配置
     */
    @Select("SELECT * FROM system_config WHERE config_key = #{configKey} AND is_active = 1")
    SystemConfig findByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置键获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值
     */
    @Select("SELECT config_value FROM system_config WHERE config_key = #{configKey} AND is_active = 1")
    String getConfigValue(@Param("configKey") String configKey);

    /**
     * 根据配置分组查询配置列表
     * 
     * @param configGroup 配置分组
     * @return 配置列表
     */
    @Select("SELECT * FROM system_config WHERE config_group = #{configGroup} AND is_active = 1 ORDER BY config_key")
    List<SystemConfig> findByConfigGroup(@Param("configGroup") String configGroup);

    /**
     * 根据配置类型查询配置列表
     * 
     * @param configType 配置类型
     * @return 配置列表
     */
    @Select("SELECT * FROM system_config WHERE config_type = #{configType} AND is_active = 1 ORDER BY config_group, config_key")
    List<SystemConfig> findByConfigType(@Param("configType") String configType);

    /**
     * 查询所有有效的配置
     * 
     * @return 所有有效配置列表
     */
    @Select("SELECT * FROM system_config WHERE is_active = 1 ORDER BY config_group, config_key")
    List<SystemConfig> findAllActiveConfigs();

    /**
     * 查询所有配置分组
     * 
     * @return 配置分组列表
     */
    @Select("SELECT DISTINCT config_group FROM system_config WHERE is_active = 1 ORDER BY config_group")
    List<String> findAllConfigGroups();

    /**
     * 根据关键词搜索配置
     * 
     * @param keyword 关键词
     * @return 匹配的配置列表
     */
    @Select("SELECT * FROM system_config WHERE is_active = 1 " +
            "AND (config_key LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY config_group, config_key")
    List<SystemConfig> searchConfigs(@Param("keyword") String keyword);

    /**
     * 更新配置值
     * 
     * @param configKey 配置键
     * @param configValue 新的配置值
     * @return 更新行数
     */
    @Update("UPDATE system_config SET config_value = #{configValue}, updated_time = CURRENT_TIMESTAMP " +
            "WHERE config_key = #{configKey}")
    int updateConfigValue(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * 启用或禁用配置
     * 
     * @param configKey 配置键
     * @param isActive 是否启用
     * @return 更新行数
     */
    @Update("UPDATE system_config SET is_active = #{isActive}, updated_time = CURRENT_TIMESTAMP " +
            "WHERE config_key = #{configKey}")
    int updateConfigStatus(@Param("configKey") String configKey, @Param("isActive") Boolean isActive);

    /**
     * 检查配置键是否已存在
     * 
     * @param configKey 配置键
     * @param excludeId 排除的配置ID（用于更新时检查）
     * @return 存在的数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM system_config WHERE config_key = #{configKey} " +
            "<if test='excludeId != null'>" +
            "AND config_id != #{excludeId} " +
            "</if>" +
            "</script>")
    int countByConfigKey(@Param("configKey") String configKey, @Param("excludeId") Long excludeId);

    /**
     * 获取字符串类型配置值
     * 
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    @Select("SELECT COALESCE(config_value, #{defaultValue}) FROM system_config " +
            "WHERE config_key = #{configKey} AND is_active = 1")
    String getStringValue(@Param("configKey") String configKey, @Param("defaultValue") String defaultValue);

    /**
     * 获取整数类型配置值
     * 
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    @Select("SELECT COALESCE(CAST(config_value AS INTEGER), #{defaultValue}) FROM system_config " +
            "WHERE config_key = #{configKey} AND config_type = 'integer' AND is_active = 1")
    Integer getIntegerValue(@Param("configKey") String configKey, @Param("defaultValue") Integer defaultValue);

    /**
     * 获取小数类型配置值
     * 
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    @Select("SELECT COALESCE(CAST(config_value AS DECIMAL), #{defaultValue}) FROM system_config " +
            "WHERE config_key = #{configKey} AND config_type = 'decimal' AND is_active = 1")
    Double getDecimalValue(@Param("configKey") String configKey, @Param("defaultValue") Double defaultValue);

    /**
     * 获取布尔类型配置值
     * 
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    @Select("SELECT COALESCE(" +
            "CASE WHEN LOWER(config_value) IN ('true', '1', 'yes', 'on') THEN 1 ELSE 0 END, " +
            "#{defaultValue}) FROM system_config " +
            "WHERE config_key = #{configKey} AND config_type = 'boolean' AND is_active = 1")
    Boolean getBooleanValue(@Param("configKey") String configKey, @Param("defaultValue") Boolean defaultValue);

    /**
     * 批量更新配置值
     * 
     * @param configs 配置列表
     * @return 更新行数
     */
    @Update("<script>" +
            "<foreach collection='configs' item='config' separator=';'>" +
            "UPDATE system_config SET config_value = #{config.configValue}, updated_time = CURRENT_TIMESTAMP " +
            "WHERE config_key = #{config.configKey}" +
            "</foreach>" +
            "</script>")
    int batchUpdateConfigValues(@Param("configs") List<SystemConfig> configs);

    /**
     * 统计各分组的配置数量
     * 
     * @return 统计结果
     */
    @Select("SELECT config_group, COUNT(*) as count FROM system_config " +
            "WHERE is_active = 1 GROUP BY config_group ORDER BY config_group")
    List<SystemConfig> countByConfigGroup();

    /**
     * 查询系统关键配置
     * 
     * @return 系统关键配置列表
     */
    @Select("SELECT * FROM system_config WHERE config_group = 'system' AND is_active = 1 ORDER BY config_key")
    List<SystemConfig> findSystemConfigs();

    /**
     * 查询业务配置
     * 
     * @return 业务配置列表
     */
    @Select("SELECT * FROM system_config WHERE config_group IN ('borrow', 'fine', 'reservation') " +
            "AND is_active = 1 ORDER BY config_group, config_key")
    List<SystemConfig> findBusinessConfigs();

    /**
     * 重置配置为默认值（如果有定义的话）
     * 
     * @param configKey 配置键
     * @return 更新行数
     */
    @Update("UPDATE system_config SET config_value = " +
            "(CASE config_key " +
            "WHEN 'borrow.default_days' THEN '30' " +
            "WHEN 'borrow.max_books_student' THEN '5' " +
            "WHEN 'borrow.max_books_teacher' THEN '10' " +
            "WHEN 'fine.overdue_fee_per_day' THEN '0.50' " +
            "ELSE config_value END), " +
            "updated_time = CURRENT_TIMESTAMP " +
            "WHERE config_key = #{configKey}")
    int resetConfigToDefault(@Param("configKey") String configKey);

    /**
     * 获取配置的最后更新时间
     * 
     * @param configKey 配置键
     * @return 最后更新时间
     */
    @Select("SELECT updated_time FROM system_config WHERE config_key = #{configKey}")
    String getConfigLastUpdateTime(@Param("configKey") String configKey);
} 