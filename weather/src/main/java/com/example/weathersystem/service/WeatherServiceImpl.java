package com.example.weathersystem.service;

import com.example.weathersystem.entity.City;
import com.example.weathersystem.entity.WeatherInfo;
import com.example.weathersystem.mapper.CityMapper;
import com.example.weathersystem.util.BaiduMapUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherServiceImpl.class);

    @Resource
    private BaiduMapUtil baiduMapUtil;

    @Resource
    private CityMapper cityMapper;

    @Override
    public WeatherInfo getWeatherByCityId(Integer cityId) {
        City city = cityMapper.selectById(cityId);
        if (city == null) {
            log.warn("城市ID {} 不存在", cityId);
            return null;
        }

        try {
            JsonNode weatherResult = baiduMapUtil.getWeather(city.getCityName(), city.getCityCode());
            if (weatherResult == null) {
                log.warn("获取 {} 天气数据为空", city.getCityName());
                return null;
            }
            return parseWeatherInfo(weatherResult, cityId);
        } catch (Exception e) {
            log.error("获取 {} 天气数据失败: {}", city.getCityName(), e.getMessage(), e);
            return null;
        }
    }

    private WeatherInfo parseWeatherInfo(JsonNode result, Integer cityId) {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setCityId(cityId);
        weatherInfo.setUpdateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        try {
            // 解析当前天气 - 使用百度API实际字段名
            JsonNode now = result.get("now");
            if (now != null) {
                weatherInfo.setTemperature(safeGetText(now, "temp", "--") + "°C");
                weatherInfo.setWeather(safeGetText(now, "text", "未知"));
                weatherInfo.setWindDirection(safeGetText(now, "wind_dir", "--"));
                weatherInfo.setWindPower(safeGetText(now, "wind_class", "--"));
                weatherInfo.setHumidity(safeGetText(now, "rh", "--") + "%");
                weatherInfo.setFeelTemp(safeGetText(now, "feels_like", "--") + "°C");
            }

            // 解析预警信息
            JsonNode alerts = result.get("alerts");
            if (alerts != null && alerts.isArray() && alerts.size() > 0) {
                StringBuilder warning = new StringBuilder();
                for (JsonNode alert : alerts) {
                    JsonNode title = alert.get("title");
                    if (title != null) {
                        warning.append(title.asText()).append("; ");
                    }
                }
                weatherInfo.setWarning(warning.length() > 0 ? warning.toString() : "暂无预警");
            } else {
                weatherInfo.setWarning("暂无预警");
            }

            // 解析未来天气预报（取明天）- 使用百度API实际字段名 high/low
            JsonNode forecasts = result.get("forecasts");
            if (forecasts != null && forecasts.isArray() && forecasts.size() > 1) {
                JsonNode tomorrow = forecasts.get(1);
                if (tomorrow != null) {
                    String forecast = safeGetText(tomorrow, "text_day", "--") + "转" +
                            safeGetText(tomorrow, "text_night", "--") + ", " +
                            safeGetText(tomorrow, "low", "--") + "°C ~ " +
                            safeGetText(tomorrow, "high", "--") + "°C";
                    weatherInfo.setForecast(forecast);
                }
            } else {
                weatherInfo.setForecast("暂无预报数据");
            }

        } catch (Exception e) {
            log.error("解析天气数据失败: {}", e.getMessage(), e);
        }

        return weatherInfo;
    }

    private String safeGetText(JsonNode node, String field, String defaultValue) {
        JsonNode child = node.get(field);
        return (child != null && !child.isNull()) ? child.asText() : defaultValue;
    }
}