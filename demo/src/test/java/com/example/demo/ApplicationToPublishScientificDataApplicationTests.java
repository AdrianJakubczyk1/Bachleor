package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = ApplicationToPublishScientificDataApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.profiles.active=test",
                // turn off all DataSource / JdbcTemplate / Spring‑Session JDBC
                "spring.autoconfigure.exclude= org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,"
        }
)
@ActiveProfiles("test")
class ApplicationToPublishScientificDataApplicationTests {

    @Test
    void contextLoads() {
    }
}