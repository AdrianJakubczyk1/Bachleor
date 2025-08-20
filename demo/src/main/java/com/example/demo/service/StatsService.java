package com.example.demo.service;

import com.example.demo.persistent.model.AppStatistic;
import com.example.demo.persistent.repository.CommentRepository;
import com.example.demo.persistent.repository.PostRepository;
import com.example.demo.persistent.repository.AppStatisticRepository;
import com.example.demo.persistent.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final AppStatisticRepository statsRepository;

    public StatsService(PostRepository postRepository,
                        UserRepository userRepository,
                        CommentRepository commentRepository,
                        AppStatisticRepository statsRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.statsRepository = statsRepository;
    }

    public void updateStatistics() {
        updateStat("postCount",    postRepository.count());
        updateStat("teacherCount", userRepository.countByRole("TEACHER"));
        updateStat("userCount",    userRepository.countByRole("USER"));
        updateStat("commentCount", commentRepository.count());
    }

    private void updateStat(String name, long value) {
        AppStatistic stat = statsRepository.findByName(name)
                .orElseGet(() -> {
                    AppStatistic s = new AppStatistic();
                    s.setStatName(name);
                    return s;
                });
        stat.setStatValue(value);
        stat.setTimestamp(LocalDateTime.now());
        statsRepository.save(stat);
    }

    public Map<String, Long> getLatestStats() {
        return statsRepository.findAll().stream()
                .collect(Collectors.toMap(
                        AppStatistic::getStatName,
                        AppStatistic::getStatValue
                ));
    }
}