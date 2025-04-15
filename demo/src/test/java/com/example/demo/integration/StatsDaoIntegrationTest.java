package com.example.demo.integration;

import com.example.demo.temp.repository.StatsDaoImplRepository;
import com.example.demo.temp.model.AppStatistic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class StatsDaoIntegrationTest {

    @Autowired
    private StatsDaoImplRepository statsDao;

    @Test
    public void testSaveAndRetrieveStat() {
        AppStatistic stat = new AppStatistic();
        stat.setStatName("testStat");
        stat.setStatValue(123L);
        stat.setTimestamp(LocalDateTime.now());

        statsDao.saveStat(stat);

        List<AppStatistic> stats = statsDao.findAllStats();

        boolean found = stats.stream().anyMatch(s -> "testStat".equals(s.getStatName())
                && s.getStatValue() == 123L);
        assertTrue(found, "The inserted statistic should be retrievable from the database.");
    }
}
