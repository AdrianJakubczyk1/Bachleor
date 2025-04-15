package com.example.demo.service;

import com.example.demo.temp.repository.StatsDaoImplRepository;
import com.example.demo.temp.model.AppStatistic;
import com.example.demo.persistent.repository.CommentRepository;
import com.example.demo.persistent.repository.PostRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import java.util.stream.Stream;

@Service
public class StatsService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    @Qualifier("statsDaoImpl")
    private StatsDaoImplRepository statsDao;

    public void updateStatistics() {
        long postCount = postRepository.count();
        long teacherCount = userRepository.countByRole("TEACHER");
        long userCount = userRepository.countByRole("USER");
        long commentCount = commentRepository.count();

        updateStat("postCount", postCount);
        updateStat("teacherCount", teacherCount);
        updateStat("userCount", userCount);
        updateStat("commentCount", commentCount);
    }

    private void updateStat(String statName, long statValue) {
        List<AppStatistic> stats = statsDao.findAllStats().stream()
                .filter(s -> statName.equals(s.getStatName()))
                .collect(Collectors.toList());
        AppStatistic stat;
        if (stats.isEmpty()) {
            stat = new AppStatistic();
            stat.setStatName(statName);
        } else {
            stat = stats.stream()
                    .max(Comparator.comparing(AppStatistic::getTimestamp))
                    .orElse(new AppStatistic());
        }
        stat.setStatValue(statValue);
        stat.setTimestamp(LocalDateTime.now());
        statsDao.saveStat(stat);
    }

    public Map<String, Long> getLatestStats() {
        List<AppStatistic> stats = StreamSupport.stream(statsDao.findAllStats().spliterator(), false)
                .collect(Collectors.toList());
        return stats.stream().collect(Collectors.toMap(
                AppStatistic::getStatName,
                AppStatistic::getStatValue,
                (val1, val2) -> val1
        ));
    }
}