package com.second.hand.trading.server.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据源配置
 */
@Configuration
@MapperScan("com.second.hand.trading.server.dao")
public class DataSourceConfig {
    
    /**
     * 配置Druid数据源
     */
    @Bean
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }
    
    // 删除重复的mybatisPlusInterceptor Bean定义
    // MyBatis-Plus相关配置已移至MybatisPlusConfig类
} 