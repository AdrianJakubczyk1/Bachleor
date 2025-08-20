package com.example.demo;
import com.example.demo.persistent.model.AppStatistic;
import com.example.demo.persistent.repository.AppStatisticRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminDashboardController {

    private final AppStatisticRepository statsRepository;

    public AdminDashboardController(AppStatisticRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        List<AppStatistic> stats = statsRepository.findAll().stream().toList();

        List<String> statsLabels = stats.stream()
                .map(AppStatistic::getStatName)
                .collect(Collectors.toList());
        List<Long> statsValues = stats.stream()
                .map(AppStatistic::getStatValue)
                .collect(Collectors.toList());

        model.addAttribute("statsLabels", statsLabels);
        model.addAttribute("statsValues", statsValues);

        return "adminDashboard";
    }
}