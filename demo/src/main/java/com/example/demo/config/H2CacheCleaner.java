package com.example.demo.config;

import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class H2CacheCleaner {

    private final JdbcTemplate tempJdbcTemplate;

    public H2CacheCleaner(@Qualifier("tempDataSource") DataSource tempDataSource) {
        this.tempJdbcTemplate = new JdbcTemplate(tempDataSource);
    }

    @PreDestroy
    public void clearH2Cache() {
        try {
            tempJdbcTemplate.execute("DROP ALL OBJECTS");
            System.out.println("H2 cache cleared with DROP ALL OBJECTS");
        } catch (Exception ex) {
            System.err.println("Error while clearing H2 cache: " + ex.getMessage());
        }


    }
}