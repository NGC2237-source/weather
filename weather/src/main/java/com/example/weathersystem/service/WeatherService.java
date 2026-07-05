package com.example.weathersystem.service;

import com.example.weathersystem.entity.WeatherInfo;

public interface WeatherService {
    WeatherInfo getWeatherByCityId(Integer cityId);
}
