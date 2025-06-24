package com.library.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus 配置类
 * 配置分页插件、SQL性能监控等功能
 * 
 * @author Library Management System
 * @version 1.0.0
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 配置MyBatis Plus拦截器
     * 包含分页插件等功能
     * 
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.SQLITE);
        // 设置单页最大查询数量，防止恶意查询
        paginationInnerInterceptor.setMaxLimit(1000L);
        // 当超过最大页数时，回到首页
        paginationInnerInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        
        return interceptor;
    }
} 