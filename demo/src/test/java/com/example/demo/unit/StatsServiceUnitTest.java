package com.example.demo.unit;

import com.example.demo.service.StatsService;
import com.example.demo.temp.model.AppStatistic;
import com.example.demo.temp.repository.StatsDaoImplRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class StatsServiceUnitTest {

    @Mock
    private StatsDaoImplRepository statsDao;

    @InjectMocks
    private StatsService statsService;

    @Test
    public void testGetLatestStats() {
        // Create a sample AppStatistic
        AppStatistic stat = new AppStatistic();
        stat.setId(1L);
        stat.setStatName("postCount");
        stat.setStatValue(50L);
        stat.setTimestamp(LocalDateTime.now());

        // When the DAO's findAllStats is called, return sample list.
        when(statsDao.findAllStats()).thenReturn(Collections.singletonList(stat));

        Map<String, Long> latestStats = statsService.getLatestStats();

        assertEquals(50L, latestStats.get("postCount"));
    }
}
