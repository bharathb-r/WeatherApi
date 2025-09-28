package com.example.WeatherAPI.client;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.WeatherAPI.dto.CurrentWeatherDto;
import com.example.WeatherAPI.dto.ForecastDto;
import com.example.WeatherAPI.dto.ForecastDayDto;
import com.example.WeatherAPI.dto.LocationDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class OpenWeatherClient {
    private final WebClient webClient;
    private final String apiKey;
    private final Logger log = LoggerFactory.getLogger(OpenWeatherClient.class);

    public OpenWeatherClient(WebClient.Builder builder, @Value("${openweather.api.key}") String apiKey) {
        this.webClient = builder.baseUrl("https://api.openweathermap.org").build();
        this.apiKey = apiKey;
    }

    public List<LocationDto> geocode(String q, int limit) {
        try {
            JsonNode[] arr = webClient.get()
                    .uri(uri -> uri.path("/geo/1.0/direct")
                            .queryParam("q", q)
                            .queryParam("limit", limit)
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve().bodyToMono(JsonNode[].class).block();
            List<LocationDto> out = new ArrayList<>();
            if (arr != null) {
                for (JsonNode node : arr) {
                    String name = node.path("name").asText("");
                    String country = node.path("country").asText("");
                    String state = node.path("state").asText("");
                    double lat = node.path("lat").asDouble();
                    double lon = node.path("lon").asDouble();
                    out.add(new LocationDto(name, country, state, lat, lon));
                }
            }
            return out;
        } catch (Exception ex) {
            log.warn("OpenWeather geocode failed: {}", ex.getMessage());
            return List.of();
        }
    }

    public Optional<CurrentWeatherDto> currentByLatLon(double lat, double lon) {
        try {
            JsonNode node = webClient.get()
                    .uri(uri -> uri.path("/data/2.5/weather")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("units", "metric")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve().bodyToMono(JsonNode.class).block();

            if (node == null) return Optional.empty();

            double temp = node.path("main").path("temp").asDouble(Double.NaN);
            double feels = node.path("main").path("feels_like").asDouble(Double.NaN);
            int humidity = node.path("main").path("humidity").asInt(-1);
            double pressure = node.path("main").path("pressure").asDouble(Double.NaN);
            double windSpeed = node.path("wind").path("speed").asDouble(Double.NaN);
            String desc = node.path("weather").isArray() && node.path("weather").size() > 0
                    ? node.path("weather").get(0).path("description").asText("") : "";

            CurrentWeatherDto dto = new CurrentWeatherDto();
            dto.setTemperatureC(temp);
            dto.setFeelsLikeC(feels);
            dto.setHumidity(humidity == -1 ? null : humidity);
            dto.setPressure(pressure);
            dto.setWindSpeedMps(windSpeed);
            dto.setDescription(desc);
            dto.setProvider("openweathermap");
            dto.setTimestamp(java.time.Instant.now());
            return Optional.of(dto);
        } catch (Exception ex) {
            log.warn("OpenWeather current failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    // We provide a very simple forecast extractor (3-hourly -> daily min/max)
    public Optional<ForecastDto> forecastByLatLon(double lat, double lon, int days, String locationName) {
        try {
            JsonNode node = webClient.get()
                    .uri(uri -> uri.path("/data/2.5/forecast")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("units", "metric")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve().bodyToMono(JsonNode.class).block();
            if (node == null) return Optional.empty();

            // aggregate by date
            java.time.LocalDate today = java.time.LocalDate.now();
            java.util.Map<java.time.LocalDate, java.util.List<JsonNode>> perDay = new java.util.HashMap<>();
            for (JsonNode item : node.path("list")) {
                String dtTxt = item.path("dt_txt").asText(""); // format "YYYY-MM-DD HH:MM:SS"
                java.time.LocalDate date = java.time.LocalDate.parse(dtTxt.substring(0, 10));
                if (!date.isBefore(today) && perDay.size() < days) {
                    perDay.computeIfAbsent(date, d -> new java.util.ArrayList<>()).add(item);
                }
            }
            java.util.List<ForecastDayDto> list = new java.util.ArrayList<>();
            perDay.keySet().stream().sorted().limit(days).forEach(d -> {
                var arr = perDay.get(d);
                double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
                int humSum = 0, humCount = 0;
                String desc = "";
                for (JsonNode it : arr) {
                    double t = it.path("main").path("temp").asDouble();
                    min = Math.min(min, t); max = Math.max(max, t);
                    humSum += it.path("main").path("humidity").asInt(0); humCount++;
                    if (desc.isEmpty() && it.path("weather").isArray() && it.path("weather").size() > 0)
                        desc = it.path("weather").get(0).path("description").asText("");
                }
                ForecastDayDto fd = new ForecastDayDto();
                fd.setDate(d);
                fd.setMinTempC(min == Double.MAX_VALUE ? 0 : min);
                fd.setMaxTempC(max == -Double.MAX_VALUE ? 0 : max);
                fd.setAvgHumidity(humCount==0?null:humSum/humCount);
                fd.setDescription(desc);
                list.add(fd);
            });

            ForecastDto out = new ForecastDto();
            out.setProvider("openweathermap");
            out.setLocationName(locationName);
            out.setDays(list);
            return Optional.of(out);
        } catch (Exception ex) {
            log.warn("OpenWeather forecast failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}

