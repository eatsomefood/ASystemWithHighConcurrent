package com.star.highconcurrent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
public class TxConfig {
    // 注入数据源（由 Spring 自动配置，如 HikariCP）
    private final DataSource dataSource;

    public TxConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // 注册事务管理器
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    // 注册 TransactionTemplate（可选，简化编程式事务代码）
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }
}