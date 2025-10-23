package com.example.orden_pedido.infrastructure.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;


@Configuration
@EnableJpaRepositories(
        basePackages = "com.integraciones.orden_pedido.infraestructure.adapter.out.adb",
        entityManagerFactoryRef = "adbEntityManagerFactory",
        transactionManagerRef = "adbTransactionManager"
)
public class ADBDataSourceConfig {

    @Primary
    @Bean(name = "adbDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.adb")
    public DataSource adbDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "adbEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean adbEntityManagerFactory(
            @Qualifier("adbDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.integraciones.orden_pedido.infraestructure.adapter.out.adb.entities");
        em.setPersistenceUnitName("adb");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
    }

    @Bean(name = "adbTransactionManager")
    public PlatformTransactionManager adbTransactionManager(
            @Qualifier("adbEntityManagerFactory") EntityManagerFactory adbEntityManagerFactory) {
        return new JpaTransactionManager(adbEntityManagerFactory);
    }

    @Bean(name = "adbJdbcTemplate")
    public JdbcTemplate adbJdbcTemplate(@Qualifier("adbDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}