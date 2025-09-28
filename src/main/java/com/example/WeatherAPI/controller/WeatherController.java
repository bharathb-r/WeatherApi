package com.example.WeatherAPI.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.WeatherAPI.dto.CurrentWeatherDto;
import com.example.WeatherAPI.dto.ForecastDto;
import com.example.WeatherAPI.dto.LocationDto;
import com.example.WeatherAPI.service.WeatherService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/")
public class WeatherController {

    private final WeatherService service;

    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @Operation(summary = "Search locations (autocomplete)")
    @GetMapping("/locations/search")
    public ResponseEntity<List<LocationDto>> searchLocations(@RequestParam("q") String query) {
        return ResponseEntity.ok(service.searchLocations(query));
    }

    @Operation(summary = "Get current weather for a location (city name or query)")
    @GetMapping("/weather/current")
    public ResponseEntity<CurrentWeatherDto> current(@RequestParam("location") String location) {
        return ResponseEntity.ok(service.getCurrentWeather(location));
    }

    @Operation(summary = "Get daily forecast (n days). Max recommended 7.")
    @GetMapping("/weather/forecast")
    public ResponseEntity<ForecastDto> forecast(@RequestParam("location") String location,
                                                @RequestParam(value = "days", defaultValue = "3") int days) {
        if (days < 1) days = 1;
        if (days > 10) days = 10;
        return ResponseEntity.ok(service.getForecast(location, days));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> out = new HashMap<>();
        out.put("status", "UP");
        out.put("timestamp", java.time.Instant.now().toString());
        return ResponseEntity.ok(out);
    }
}
