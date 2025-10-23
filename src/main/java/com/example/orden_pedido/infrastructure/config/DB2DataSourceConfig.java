package com.example.orden_pedido.infrastructure.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DB2DataSourceConfig {

    @Bean(name = "db2DataSource")
    @ConfigurationProperties(prefix="spring.datasource.db2")
    public HikariDataSource  db2DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "db2JdbcTemplate")
    public JdbcTemplate db2JdbcTemplate(@Qualifier("db2DataSource") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
    @Bean(name = "db2TransactionManager")
    public PlatformTransactionManager db2TransactionManager(@Qualifier("db2DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


}

