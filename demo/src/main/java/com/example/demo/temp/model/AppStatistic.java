package com.example.demo.temp.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("app_statistics")
public class AppStatistic {

    @Id
    private Long id;

    private String statName;
    private Long statValue;
    private LocalDateTime timestamp;
    private String statText;

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStatName() {
        return statName;
    }
    public void setStatName(String statName) {
        this.statName = statName;
    }
    public Long getStatValue() {
        return statValue;
    }
    public void setStatValue(Long statValue) {
        this.statValue = statValue;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public String getStatText() {
        return statText;
    }
    public void setStatText(String statText) {
        this.statText = statText;
    }
}
