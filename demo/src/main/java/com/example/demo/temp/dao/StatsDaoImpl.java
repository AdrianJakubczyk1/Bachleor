package com.example.demo.temp.dao;

import com.example.demo.temp.model.AppStatistic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo.temp.repository.StatsDaoImplRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class StatsDaoImpl implements StatsDaoImplRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public StatsDaoImpl(@Qualifier("tempJdbcTemplate") NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<AppStatistic> findAllStats() {
        String sql = "SELECT id, stat_name, stat_value, timestamp FROM app_statistics";
        return jdbcOperations.query(sql, new BeanPropertyRowMapper<>(AppStatistic.class));
    }

    @Override
    public void saveStat(AppStatistic stat) {

        String sql = "INSERT INTO app_statistics (stat_name, stat_value, timestamp) " +
                "VALUES (:statName, :statValue, :timestamp)";
        Map<String, Object> params = new HashMap<>();
        params.put("statName", stat.getStatName());
        params.put("statValue", stat.getStatValue());
        params.put("timestamp", stat.getTimestamp());
        jdbcOperations.update(sql, params);
    }

    @Override
    public void saveStatText(AppStatistic stat) {
        String sql = "INSERT INTO app_statistics (stat_name, stat_text, timestamp) " +
                "VALUES (:statName, :statText, :timestamp)";
        Map<String, Object> params = new HashMap<>();
        params.put("statName", stat.getStatName());
        params.put("statText", stat.getStatText());
        params.put("timestamp", stat.getTimestamp());
        jdbcOperations.update(sql, params);
    }
}

