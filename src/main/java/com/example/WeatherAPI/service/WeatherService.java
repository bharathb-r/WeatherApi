package com.example.WeatherAPI.service;


import java.util.List;
import java.util.Optional;

import com.example.WeatherAPI.client.OpenWeatherClient;
import com.example.WeatherAPI.client.WeatherApiClient;
import com.example.WeatherAPI.dto.CurrentWeatherDto;
import com.example.WeatherAPI.dto.ForecastDto;
import com.example.WeatherAPI.dto.LocationDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    private final OpenWeatherClient openWeatherClient;
    private final WeatherApiClient weatherApiClient;
    private final Logger log = LoggerFactory.getLogger(WeatherService.class);

    public WeatherService(OpenWeatherClient o, WeatherApiClient w) {
        this.openWeatherClient = o;
        this.weatherApiClient = w;
    }

    @Cacheable(value = "locations", key = "#query")
    public List<LocationDto> searchLocations(String query) {
        // try WeatherAPI search first then OpenWeather fallback
        List<LocationDto> res = weatherApiClient.searchLocations(query);
        if (res == null || res.isEmpty()) {
            res = openWeatherClient.geocode(query, 5);
        }
        return res;
    }

    @Cacheable(value = "currentWeather", key = "#location.toLowerCase()")
    public CurrentWeatherDto getCurrentWeather(String location) {
        // 1) Try WeatherAPI direct current
        Optional<CurrentWeatherDto> wapi = weatherApiClient.currentByQuery(location);
        // 2) Try OpenWeather (needs geo) by geocoding
        if (wapi.isPresent()) {
            return wapi.get();
        } else {
            List<LocationDto> locs = searchLocations(location);
            if (locs.isEmpty()) throw new RuntimeException("Location not found: " + location);
            LocationDto chosen = locs.get(0);
            Optional<CurrentWeatherDto> owp = openWeatherClient.currentByLatLon(chosen.getLat(), chosen.getLon());
            if (owp.isPresent()) return owp.get();
            throw new RuntimeException("All upstream weather providers failed for: " + location);
        }
    }

    @Cacheable(value = "forecast", key = "#location.toLowerCase() + ':' + #days")
    public ForecastDto getForecast(String location, int days) {
        // prefer WeatherAPI for daily forecast (if key present), else fallback to OpenWeather
        Optional<ForecastDto> wapi = weatherApiClient.forecastByQuery(location, days);
        if (wapi.isPresent()) return wapi.get();

        List<LocationDto> locs = searchLocations(location);
        if (locs.isEmpty()) throw new RuntimeException("Location not found: " + location);
        LocationDto chosen = locs.get(0);

        Optional<ForecastDto> owp = openWeatherClient.forecastByLatLon(chosen.getLat(), chosen.getLon(), days, chosen.getName());
        if (owp.isPresent()) return owp.get();

        throw new RuntimeException("All upstream forecast providers failed for: " + location);
    }
}

