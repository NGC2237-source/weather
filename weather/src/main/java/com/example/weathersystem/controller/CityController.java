package com.example.weathersystem.controller;

import com.example.weathersystem.entity.City;
import com.example.weathersystem.entity.WeatherInfo;
import com.example.weathersystem.service.CityService;
import com.example.weathersystem.service.WeatherService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/city")
public class CityController {

    private static final Logger log = LoggerFactory.getLogger(CityController.class);

    @Resource
    private CityService cityService;

    @Resource
    private WeatherService weatherService;

    /** 测试接口 */
    @GetMapping("/test")
    public String testApi() {
        return "接口连通成功";
    }

    /** 城市列表接口 */
    @GetMapping("/list")
    public List<City> getCityList() {
        return cityService.getAllCities();
    }

    /** 城市搜索接口 - 优先从本地数据库搜索，兜底使用百度API */
    @GetMapping("/search")
    public List<String> searchCity(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        keyword = keyword.trim();

        // 优先从本地数据库搜索
        List<City> localResults = cityService.searchCities(keyword);
        if (localResults != null && !localResults.isEmpty()) {
            return localResults.stream()
                    .map(City::getCityName)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 本地无结果时，返回空列表（避免频繁调用外部API）
        return List.of();
    }

    /** 根据城市id查天气 */
    @GetMapping("/weather/{cityId}")
    public ResponseEntity<?> getWeather(@PathVariable Integer cityId) {
        try {
            WeatherInfo weather = weatherService.getWeatherByCityId(cityId);
            if (weather != null) {
                return ResponseEntity.ok(weather);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "获取天气数据失败，请稍后重试");
                return ResponseEntity.status(500).body(error);
            }
        } catch (Exception e) {
            log.error("获取天气异常, cityId={}: {}", cityId, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "服务器内部错误");
            return ResponseEntity.status(500).body(error);
        }
    }

    /** 根据城市名称查天气 */
    @GetMapping("/weather/byName/{cityName}")
    public ResponseEntity<?> getWeatherByName(@PathVariable String cityName) {
        try {
            City city = cityService.getCityByName(cityName);
            if (city != null) {
                WeatherInfo weather = weatherService.getWeatherByCityId(city.getId());
                if (weather != null) {
                    return ResponseEntity.ok(weather);
                }
            }
            Map<String, String> error = new HashMap<>();
            error.put("error", "未找到该城市的天气数据");
            return ResponseEntity.status(404).body(error);
        } catch (Exception e) {
            log.error("获取天气异常, cityName={}: {}", cityName, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "服务器内部错误");
            return ResponseEntity.status(500).body(error);
        }
    }
}
