package com.example.demo.temp.repository;

import com.example.demo.temp.model.AppStatistic;
import java.util.List;

public interface StatsDaoImplRepository {
    List<AppStatistic> findAllStats();

    void saveStat(AppStatistic stat);

    void saveStatText(AppStatistic stat);
}