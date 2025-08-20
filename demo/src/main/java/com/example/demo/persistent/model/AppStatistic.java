package com.example.demo.persistent.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class AppStatistic implements Serializable {
    private static final long serialVersionUID = 1L;

    private String statName;
    private Long statValue;
    private LocalDateTime timestamp;
    private String statText;

    public AppStatistic() {}

    public AppStatistic(String statName, Long statValue, LocalDateTime timestamp, String statText) {
        this.statName = statName;
        this.statValue = statValue;
        this.timestamp = timestamp;
        this.statText = statText;
    }

    public String getStatName() { return statName; }
    public void setStatName(String statName) { this.statName = statName; }

    public Long getStatValue() { return statValue; }
    public void setStatValue(Long statValue) { this.statValue = statValue; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatText() { return statText; }
    public void setStatText(String statText) { this.statText = statText; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppStatistic)) return false;
        AppStatistic that = (AppStatistic) o;
        return Objects.equals(statName, that.statName)
                && Objects.equals(statValue, that.statValue)
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(statText, that.statText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statName, statValue, timestamp, statText);
    }

    @Override
    public String toString() {
        return "AppStatistic{" +
                "statName='" + statName + '\'' +
                ", statValue=" + statValue +
                ", timestamp=" + timestamp +
                ", statText='" + statText + '\'' +
                '}';
    }
}