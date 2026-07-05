package com.example.weathersystem.util;

import com.example.weathersystem.config.BaiduMapConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Component
public class BaiduMapUtil {

    private static final Logger log = LoggerFactory.getLogger(BaiduMapUtil.class);

    @Resource
    private BaiduMapConfig baiduMapConfig;

    @Resource
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 常用城市行政区划代码映射表（作为兜底方案） */
    private static final Map<String, String> CITY_DISTRICT_MAP = new LinkedHashMap<>();
    static {
        CITY_DISTRICT_MAP.put("北京", "110100");
        CITY_DISTRICT_MAP.put("上海", "310100");
        CITY_DISTRICT_MAP.put("天津", "120100");
        CITY_DISTRICT_MAP.put("重庆", "500100");
        CITY_DISTRICT_MAP.put("石家庄", "130100");
        CITY_DISTRICT_MAP.put("太原", "140100");
        CITY_DISTRICT_MAP.put("呼和浩特", "150100");
        CITY_DISTRICT_MAP.put("沈阳", "210100");
        CITY_DISTRICT_MAP.put("长春", "220100");
        CITY_DISTRICT_MAP.put("哈尔滨", "230100");
        CITY_DISTRICT_MAP.put("南京", "320100");
        CITY_DISTRICT_MAP.put("杭州", "330100");
        CITY_DISTRICT_MAP.put("合肥", "340100");
        CITY_DISTRICT_MAP.put("福州", "350100");
        CITY_DISTRICT_MAP.put("南昌", "360100");
        CITY_DISTRICT_MAP.put("济南", "370100");
        CITY_DISTRICT_MAP.put("郑州", "410100");
        CITY_DISTRICT_MAP.put("武汉", "420100");
        CITY_DISTRICT_MAP.put("长沙", "430100");
        CITY_DISTRICT_MAP.put("广州", "440100");
        CITY_DISTRICT_MAP.put("南宁", "450100");
        CITY_DISTRICT_MAP.put("海口", "460100");
        CITY_DISTRICT_MAP.put("成都", "510100");
        CITY_DISTRICT_MAP.put("贵阳", "520100");
        CITY_DISTRICT_MAP.put("昆明", "530100");
        CITY_DISTRICT_MAP.put("拉萨", "540100");
        CITY_DISTRICT_MAP.put("西安", "610100");
        CITY_DISTRICT_MAP.put("兰州", "620100");
        CITY_DISTRICT_MAP.put("西宁", "630100");
        CITY_DISTRICT_MAP.put("银川", "640100");
        CITY_DISTRICT_MAP.put("乌鲁木齐", "650100");
        CITY_DISTRICT_MAP.put("香港", "810000");
        CITY_DISTRICT_MAP.put("澳门", "820000");
        CITY_DISTRICT_MAP.put("深圳", "440300");
        CITY_DISTRICT_MAP.put("珠海", "440400");
        CITY_DISTRICT_MAP.put("汕头", "440500");
        CITY_DISTRICT_MAP.put("佛山", "440600");
        CITY_DISTRICT_MAP.put("东莞", "441900");
        CITY_DISTRICT_MAP.put("中山", "442000");
        CITY_DISTRICT_MAP.put("青岛", "370200");
        CITY_DISTRICT_MAP.put("大连", "210200");
        CITY_DISTRICT_MAP.put("宁波", "330200");
        CITY_DISTRICT_MAP.put("厦门", "350200");
        CITY_DISTRICT_MAP.put("苏州", "320500");
        CITY_DISTRICT_MAP.put("无锡", "320200");
        CITY_DISTRICT_MAP.put("温州", "330300");
        CITY_DISTRICT_MAP.put("烟台", "370600");
        CITY_DISTRICT_MAP.put("泉州", "350500");
        CITY_DISTRICT_MAP.put("洛阳", "410300");
        CITY_DISTRICT_MAP.put("襄阳", "420600");
        CITY_DISTRICT_MAP.put("绵阳", "510700");
        CITY_DISTRICT_MAP.put("桂林", "450300");
        CITY_DISTRICT_MAP.put("丽江", "530700");
        CITY_DISTRICT_MAP.put("三亚", "460200");
        CITY_DISTRICT_MAP.put("秦皇岛", "130300");
        CITY_DISTRICT_MAP.put("威海", "371000");
        CITY_DISTRICT_MAP.put("淄博", "370300");
        CITY_DISTRICT_MAP.put("潍坊", "370700");
        CITY_DISTRICT_MAP.put("常州", "320400");
        CITY_DISTRICT_MAP.put("徐州", "320300");
        CITY_DISTRICT_MAP.put("唐山", "130200");
        CITY_DISTRICT_MAP.put("包头", "150200");
        CITY_DISTRICT_MAP.put("延安", "610600");
        CITY_DISTRICT_MAP.put("遵义", "520300");
        CITY_DISTRICT_MAP.put("大理", "532900");
        CITY_DISTRICT_MAP.put("开封", "410200");
    }

    /**
     * 获取天气信息（优先使用数据库中的cityCode，兜底使用映射表）
     * @param city 城市名称
     * @param cityCode 数据库中的城市区划代码（可为null）
     * @return 天气信息JSON
     */
    public JsonNode getWeather(String city, String cityCode) {
        try {
            String apiKey = baiduMapConfig.getApiKey();
            String districtId;
            if (cityCode != null && !cityCode.trim().isEmpty()) {
                districtId = cityCode.trim();
                log.debug("使用数据库区划代码: {} -> {}", city, districtId);
            } else {
                districtId = getDistrictId(city);
                log.debug("使用映射表区划代码: {} -> {}", city, districtId);
            }

            URI uri = UriComponentsBuilder
                    .fromUriString(BaiduMapConfig.WEATHER_API_URL)
                    .queryParam("district_id", districtId)
                    .queryParam("data_type", "all")
                    .queryParam("ak", apiKey)
                    .build()
                    .toUri();

            log.debug("请求天气API: {}", uri);
            String response = restTemplate.getForObject(uri, String.class);
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("status") && rootNode.get("status").asInt() == 0) {
                return rootNode.get("result");
            } else {
                String msg = rootNode.has("message") ? rootNode.get("message").asText() : "未知错误";
                log.error("百度地图API返回错误: {}", msg);
                throw new RuntimeException("百度地图API返回错误: " + msg);
            }
        } catch (Exception e) {
            log.error("获取天气信息失败, city={}: {}", city, e.getMessage(), e);
            throw new RuntimeException("获取天气信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 搜索城市
     */
    public List<String> searchCity(String query) {
        List<String> cities = new ArrayList<>();
        try {
            String apiKey = baiduMapConfig.getApiKey();
            URI uri = UriComponentsBuilder
                    .fromUriString(BaiduMapConfig.PLACE_SEARCH_API_URL)
                    .queryParam("query", query)
                    .queryParam("region", "全国")
                    .queryParam("output", "json")
                    .queryParam("ak", apiKey)
                    .build()
                    .toUri();

            String response = restTemplate.getForObject(uri, String.class);
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("status") && rootNode.get("status").asInt() == 0) {
                JsonNode results = rootNode.get("results");
                if (results != null && results.isArray()) {
                    for (JsonNode result : results) {
                        JsonNode cityNode = result.get("city");
                        if (cityNode != null) {
                            String cityName = cityNode.asText();
                            if (!cityName.isEmpty() && !cities.contains(cityName)) {
                                cities.add(cityName);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("搜索城市失败, query={}: {}", query, e.getMessage());
        }
        return cities;
    }

    /**
     * 根据城市名称获取行政区划代码（兜底方案）
     */
    public String getDistrictId(String city) {
        if (city == null || city.trim().isEmpty()) {
            return "110100";
        }
        city = city.trim();
        if (CITY_DISTRICT_MAP.containsKey(city)) {
            return CITY_DISTRICT_MAP.get(city);
        }
        for (Map.Entry<String, String> entry : CITY_DISTRICT_MAP.entrySet()) {
            if (city.contains(entry.getKey()) || entry.getKey().contains(city)) {
                return entry.getValue();
            }
        }
        log.warn("未找到城市 {} 的区划代码，默认使用北京", city);
        return "110100";
    }

    public Set<String> getSupportedCities() {
        return Collections.unmodifiableSet(CITY_DISTRICT_MAP.keySet());
    }
}