package com.example.weathersystem.entity;

public class WeatherInfo {
    private Integer id;
    private Integer cityId;
    private String temperature;
    private String weather;
    private String windDirection;
    private String windPower;
    private String humidity;
    private String feelTemp;
    private String warning;
    private String forecast;
    private String updateTime;

    public WeatherInfo() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCityId() { return cityId; }
    public void setCityId(Integer cityId) { this.cityId = cityId; }

    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }

    public String getWindPower() { return windPower; }
    public void setWindPower(String windPower) { this.windPower = windPower; }

    public String getHumidity() { return humidity; }
    public void setHumidity(String humidity) { this.humidity = humidity; }

    public String getFeelTemp() { return feelTemp; }
    public void setFeelTemp(String feelTemp) { this.feelTemp = feelTemp; }

    public String getWarning() { return warning; }
    public void setWarning(String warning) { this.warning = warning; }

    public String getForecast() { return forecast; }
    public void setForecast(String forecast) { this.forecast = forecast; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}
