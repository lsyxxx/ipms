package com.abt.db;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 数据源配置
 */
//@Configuration(proxyBeanMethods = false)
@Slf4j
@Deprecated
public class DBConfig {

    /**
     * 主数据库
     */

//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.biz")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }


//    @Primary
//    @Bean(name = "dataSource")
    public DataSource dataSource(DataSourceProperties primaryDataSourceProperties) {
        return primaryDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /**
     * 流程引擎数据库
     */
//    @Bean
//    @ConfigurationProperties("spring.datasource.flowable")
    public DataSourceProperties flowableDataSourceProperties() {
        return new DataSourceProperties();
    }


//    @Bean(name = "flowableDataSource")
    public DataSource flowableDataSource(@Qualifier("flowableDataSourceProperties") DataSourceProperties flowableDataSourceProperties) {
        log.info("配置abt flowable datasource");
        return flowableDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

//    @Bean(name = "transactionManager")
//    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    @Bean(name = "flowableTransactionManager")
    public PlatformTransactionManager flowableTransactionManager(@Qualifier("flowableDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


//    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
