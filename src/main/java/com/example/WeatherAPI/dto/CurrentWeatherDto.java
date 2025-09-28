package com.example.WeatherAPI.dto;


import java.time.Instant;

public class CurrentWeatherDto {
    private double temperatureC;
    private double feelsLikeC;
    private Integer humidity;
    private double pressure;
    private double windSpeedMps;
    private String description;
    private String provider;
    private Instant timestamp;

    public CurrentWeatherDto() {
    }

    public CurrentWeatherDto(double temperatureC, double feelsLikeC, Integer humidity, double pressure,
                             double windSpeedMps, String description, String provider, Instant timestamp) {
        this.temperatureC = temperatureC;
        this.feelsLikeC = feelsLikeC;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeedMps = windSpeedMps;
        this.description = description;
        this.provider = provider;
        this.timestamp = timestamp;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public double getFeelsLikeC() {
        return feelsLikeC;
    }

    public void setFeelsLikeC(double feelsLikeC) {
        this.feelsLikeC = feelsLikeC;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getWindSpeedMps() {
        return windSpeedMps;
    }

    public void setWindSpeedMps(double windSpeedMps) {
        this.windSpeedMps = windSpeedMps;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

