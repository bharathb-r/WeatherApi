package com.example.WeatherAPI.dto;


import java.util.List;

public class ForecastDto {
    private String locationName;
    private List<ForecastDayDto> days;
    private String provider;

    public ForecastDto() {
    }

    public ForecastDto(String locationName, List<ForecastDayDto> days, String provider) {
        this.locationName = locationName;
        this.days = days;
        this.provider = provider;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<ForecastDayDto> getDays() {
        return days;
    }

    public void setDays(List<ForecastDayDto> days) {
        this.days = days;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}

