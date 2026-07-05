package com.example.weathersystem.service.impl;

import com.example.weathersystem.entity.City;
import com.example.weathersystem.mapper.CityMapper;
import com.example.weathersystem.service.CityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    @Resource
    private CityMapper cityMapper;

    @Override
    public List<City> getAllCities() {
        return cityMapper.selectAllCity();
    }

    @Override
    public City getCityByName(String cityName) {
        return cityMapper.selectByName(cityName);
    }

    @Override
    public List<City> searchCities(String keyword) {
        return cityMapper.selectByKeyword(keyword);
    }
}
