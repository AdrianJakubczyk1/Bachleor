package com.example.demo.unit;

import com.example.demo.service.StatsService;
import com.example.demo.persistent.model.AppStatistic;
import com.example.demo.persistent.repository.AppStatisticRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class StatsServiceUnitTest {

    @Mock
    private AppStatisticRepository appStatisticRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    void testGetLatestStats() {
        // Arrange
        AppStatistic stat = new AppStatistic();
        stat.setStatName("postCount");
        stat.setStatValue(50L);
        stat.setTimestamp(LocalDateTime.now());
        stat.setStatText(null);

        when(appStatisticRepository.findAll())
                .thenReturn(Collections.singletonList(stat));

        // Act
        Map<String, Long> latest = statsService.getLatestStats();

        // Assert
        assertEquals(50L, latest.get("postCount"));
    }
}