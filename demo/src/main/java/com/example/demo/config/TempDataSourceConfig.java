package com.example.demo.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Remove @Primary from here unless specifically needed for other auto-configurations
// import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
// Correct imports needed for manual configuration
import org.springframework.data.jdbc.core.convert.*;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect; // Import this
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.session.jdbc.config.annotation.SpringSessionTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcRepositories(
        basePackages = "com.example.demo.temp.repository",
        jdbcOperationsRef = "tempJdbcTemplate",
        transactionManagerRef = "tempTransactionManager"
)
public class TempDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.temp")
    public DataSourceProperties tempDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "tempDataSource")
    // @Primary // Remove this unless absolutely needed AND you understand the implications
    public DataSource tempDataSource() {
        return tempDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "springSessionDataSource")
    @Primary
    public DataSource springSessionDataSource(@Qualifier("tempDataSource") DataSource tempDataSource) {
        return tempDataSource;
    }

    @Bean(name = "tempJdbcTemplate")
    public NamedParameterJdbcOperations tempJdbcTemplate(
            @Qualifier("tempDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "tempTransactionManager")
    @SpringSessionTransactionManager // Keep this if using Spring Session with this DB
    public PlatformTransactionManager tempTransactionManager(
            @Qualifier("tempDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}