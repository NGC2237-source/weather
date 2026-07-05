package com.example.weathersystem.mapper;

import com.example.weathersystem.entity.WeatherInfo;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WeatherMapper {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public WeatherInfo selectByCityId(Integer cityId) {
        String sql = "SELECT * FROM weather_info WHERE city_id = ?";
        List<WeatherInfo> list = jdbcTemplate.query(sql, new Object[]{cityId}, new BeanPropertyRowMapper<>(WeatherInfo.class));
        return list.isEmpty() ? null : list.get(0);
    }
}
