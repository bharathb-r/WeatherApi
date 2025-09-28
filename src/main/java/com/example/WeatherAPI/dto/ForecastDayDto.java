package com.example.WeatherAPI.dto;


import java.time.LocalDate;

public class ForecastDayDto {
    private LocalDate date;
    private double minTempC;
    private double maxTempC;
    private String description;
    private Integer avgHumidity;

    public ForecastDayDto() {
    }

    public ForecastDayDto(LocalDate date, double minTempC, double maxTempC, String description, Integer avgHumidity) {
        this.date = date;
        this.minTempC = minTempC;
        this.maxTempC = maxTempC;
        this.description = description;
        this.avgHumidity = avgHumidity;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getMinTempC() {
        return minTempC;
    }

    public void setMinTempC(double minTempC) {
        this.minTempC = minTempC;
    }

    public double getMaxTempC() {
        return maxTempC;
    }

    public void setMaxTempC(double maxTempC) {
        this.maxTempC = maxTempC;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAvgHumidity() {
        return avgHumidity;
    }

    public void setAvgHumidity(Integer avgHumidity) {
        this.avgHumidity = avgHumidity;
    }
}

