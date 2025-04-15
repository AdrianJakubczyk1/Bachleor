package com.example.demo.scheduler;

import com.example.demo.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StatisticsUpdater {

    @Autowired
    private StatsService statsService;

    @Scheduled(cron = "*/30 * * * * *")
    public void updateStats() {
        statsService.updateStatistics();
    }
}