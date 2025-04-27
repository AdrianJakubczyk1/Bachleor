package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.example.demo.ApplicationToPublishScientificDataApplication;

@SpringBootTest(
        classes = ApplicationToPublishScientificDataApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test"
)
@AutoConfigureTestDatabase(replace = Replace.ANY)
class ApplicationToPublishScientificDataApplicationTests {

    @Test
    void contextLoads() {
    }
}