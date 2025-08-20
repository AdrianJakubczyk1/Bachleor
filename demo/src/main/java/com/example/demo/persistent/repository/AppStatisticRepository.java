package com.example.demo.persistent.repository;


import com.example.demo.persistent.model.AppStatistic;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class AppStatisticRepository {
    private final IMap<String, AppStatistic> map;

    public AppStatisticRepository(HazelcastInstance hz) {
        this.map = hz.getMap("app_statistics");
    }

    /** Save or update the given statistic under its name */
    public void save(AppStatistic stat) {
        map.put(stat.getStatName(), stat);
    }

    /** Fetch the latest statistic by name */
    public Optional<AppStatistic> findByName(String name) {
        return Optional.ofNullable(map.get(name));
    }

    /** Fetch all current statistics */
    public Collection<AppStatistic> findAll() {
        return map.values();
    }
}