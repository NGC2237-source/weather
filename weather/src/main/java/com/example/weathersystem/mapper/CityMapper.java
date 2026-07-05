package com.example.weathersystem.mapper;

import com.example.weathersystem.entity.City;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jakarta.annotation.Resource;
import java.util.List;

@Repository
public class CityMapper {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<City> selectAllCity() {
        String sql = "SELECT * FROM city WHERE id IN (SELECT MIN(id) FROM city GROUP BY city_name) ORDER BY CONVERT(city_name USING gbk)";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(City.class));
    }

    public City selectByName(String cityName) {
        String sql = "SELECT * FROM city WHERE city_name = ? LIMIT 1";
        List<City> cities = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(City.class), cityName);
        return cities.isEmpty() ? null : cities.get(0);
    }

    public City selectById(Integer id) {
        String sql = "SELECT * FROM city WHERE id = ?";
        List<City> cities = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(City.class), id);
        return cities.isEmpty() ? null : cities.get(0);
    }

    public List<City> selectByKeyword(String keyword) {
        String sql = "SELECT * FROM city WHERE id IN (SELECT MIN(id) FROM city WHERE city_name LIKE ? OR pinyin LIKE ? GROUP BY city_name) ORDER BY CONVERT(city_name USING gbk)";
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(City.class), pattern, pattern);
    }
}