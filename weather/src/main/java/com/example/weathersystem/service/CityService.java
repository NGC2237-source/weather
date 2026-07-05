package com.example.weathersystem.service;

import com.example.weathersystem.entity.City;
import java.util.List;

public interface CityService {
    List<City> getAllCities();
    City getCityByName(String cityName);
    List<City> searchCities(String keyword);
}
