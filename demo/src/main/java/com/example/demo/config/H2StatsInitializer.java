package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class H2StatsInitializer {

    private final JdbcTemplate tempJdbcTemplate;

    public H2StatsInitializer(@Qualifier("tempDataSource") DataSource tempDataSource) {
        this.tempJdbcTemplate = new JdbcTemplate(tempDataSource);
    }

    @PostConstruct
    public void initializeStatsSchema() {
        try {
            ClassPathResource resource = new ClassPathResource("schema-h2.sql");
            String schemaSql = new String(Files.readAllBytes(Paths.get(resource.getURI())), StandardCharsets.UTF_8);
            tempJdbcTemplate.execute(schemaSql);
            System.out.println("Temporary H2 statistics schema initialized successfully.");
        } catch (Exception ex) {
            System.err.println("Error initializing H2 statistics schema: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}