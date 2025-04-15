package com.example.demo.temp.repository;


import com.example.demo.temp.model.AppStatistic;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface AppStatisticRepository extends CrudRepository<AppStatistic, Long> {
    List<AppStatistic> findByStatName(String statName);
}