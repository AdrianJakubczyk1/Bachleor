package com.example.demo;
import com.example.demo.temp.repository.StatsDaoImplRepository;
import com.example.demo.temp.model.AppStatistic;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {
    @Autowired
    private StatsDaoImplRepository statsDao;

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        List<AppStatistic> stats = statsDao.findAllStats();
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