package com.example.weathersystem.entity;

public class City {
    private Integer id;
    private String cityName;
    private String cityCode;
    private String province;
    private String pinyin;

    public City() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCityCode() { return cityCode; }
    public void setCityCode(String cityCode) { this.cityCode = cityCode; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getPinyin() { return pinyin; }
    public void setPinyin(String pinyin) { this.pinyin = pinyin; }
}
