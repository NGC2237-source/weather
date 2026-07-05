package com.example.weathersystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BaiduMapConfig implements WebMvcConfigurer {

    @Value("${baidu.map.api-key:ZCF5N74vyutebKV8Da3iAop9IGXh9ptn}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public static final String WEATHER_API_URL = "https://api.map.baidu.com/weather/v1/";
    public static final String PLACE_SEARCH_API_URL = "https://api.map.baidu.com/place/v2/search";

    /**
     * 注册RestTemplate为Spring Bean，便于复用和管理
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 配置跨域访问，允许前端页面跨域调用API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*");
    }
}
