package com.example.demo.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJdbcRepositories(
        basePackages = "com.example.demo.persistent.repository",
        jdbcOperationsRef = "persistentJdbcTemplate",
        transactionManagerRef = "persistentTransactionManager"
)
public class PersistentDataSourceConfig {

    @Bean
    @ConfigurationProperties("app.datasource.persistent")
    @Primary
    public DataSourceProperties persistentDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "persistentDataSource")
    @Primary
    public DataSource persistentDataSource() {
        return persistentDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "persistentJdbcTemplate")
    @Primary
    public NamedParameterJdbcTemplate persistentJdbcTemplate(
            @Qualifier("persistentDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "persistentTransactionManager")
    @Primary
    public PlatformTransactionManager persistentTransactionManager(
            @Qualifier("persistentDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    @Bean(name = "persistentJdbcAggregateTemplate")
//    @Primary
//    public JdbcAggregateTemplate persistentJdbcAggregateTemplate(
//            @Qualifier("persistentJdbcTemplate") NamedParameterJdbcTemplate jdbcOperations,
//            JdbcMappingContext mappingContext,
//            JdbcConverter jdbcConverter) {
//        return new JdbcAggregateTemplate(jdbcOperations, mappingContext, jdbcConverter);
//    }
}
