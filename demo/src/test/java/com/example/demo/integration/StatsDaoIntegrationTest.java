package com.example.demo.integration;

import com.example.demo.persistent.repository.AppStatisticRepository;
import com.example.demo.persistent.model.AppStatistic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class StatsDaoIntegrationTest {

    @Autowired
    private AppStatisticRepository statsDao;

    @Test
    public void testSaveAndRetrieveStat() {
        AppStatistic stat = new AppStatistic();
        stat.setStatName("testStat");
        stat.setStatValue(123L);
        stat.setTimestamp(LocalDateTime.now());

        statsDao.save(stat);

        // DON’T cast to List—findAll() returns a Collection
        Collection<AppStatistic> stats = statsDao.findAll();

        boolean found = stats.stream().anyMatch(s ->
                "testStat".equals(s.getStatName()) &&
                        s.getStatValue() == 123L
        );
        assertTrue(found, "The inserted statistic should be retrievable from Hazelcast.");
    }
}
