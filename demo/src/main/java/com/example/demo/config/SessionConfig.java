package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class SessionConfig {

    @Bean
    public JdbcIndexedSessionRepository sessionRepository(
            @Qualifier("springSessionDataSource") DataSource dataSource,
            @Qualifier("tempTransactionManager") PlatformTransactionManager transactionManager) {

        // Create a JdbcTemplate for the H2 datasource.
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        // Create a TransactionTemplate using your transaction manager.
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

        // Instantiate the session repository.
        JdbcIndexedSessionRepository sessionRepository = new JdbcIndexedSessionRepository(jdbcTemplate, transactionTemplate);

        return sessionRepository;
    }
}
