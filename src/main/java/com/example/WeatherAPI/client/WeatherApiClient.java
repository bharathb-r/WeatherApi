package com.example.WeatherAPI.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.WeatherAPI.dto.CurrentWeatherDto;
import com.example.WeatherAPI.dto.ForecastDayDto;
import com.example.WeatherAPI.dto.ForecastDto;
import com.example.WeatherAPI.dto.LocationDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public class WeatherApiClient {
    private final WebClient webClient;
    private final String apiKey;
    private final Logger log = LoggerFactory.getLogger(WeatherApiClient.class);

    public WeatherApiClient(WebClient.Builder builder, @Value("${weatherapi.api.key}") String apiKey) {
        this.webClient = builder.baseUrl("http://api.weatherapi.com").build();
        this.apiKey = apiKey;
    }

    public List<LocationDto> searchLocations(String q) {
        try {
            JsonNode node = webClient.get()
                    .uri(uri -> uri.path("/v1/search.json")
                            .queryParam("key", apiKey)
                            .queryParam("q", q)
                            .build())
                    .retrieve().bodyToMono(JsonNode.class).block();

            List<LocationDto> out = new ArrayList<>();
            if (node != null && node.isArray()) {
                for (JsonNode it : node) {
                    String name = it.path("name").asText("");
                    String country = it.path("country").asText("");
                    String region = it.path("region").asText("");
                    double lat = it.path("lat").asDouble();
                    double lon = it.path("lon").asDouble();
                    out.add(new LocationDto(name, country, region, lat, lon));
                }
            }
            return out;
        } catch (Exception ex) {
            log.warn("WeatherAPI location search failed: {}", ex.getMessage());
            return List.of();
        }
    }

    public Optional<CurrentWeatherDto> currentByQuery(String q) {
        try {
            JsonNode node = webClient.get()
                    .uri(uri -> uri.path("/v1/current.json")
                            .queryParam("key", apiKey)
                            .queryParam("q", q)
                            .build())
                    .retrieve().bodyToMono(JsonNode.class).block();

            if (node == null) return Optional.empty();
            JsonNode cur = node.path("current");
            double temp = cur.path("temp_c").asDouble(Double.NaN);
            double feels = cur.path("feelslike_c").asDouble(Double.NaN);
            int hum = cur.path("humidity").asInt(-1);
            double pressure = cur.path("pressure_mb").asDouble(Double.NaN);
            double wind = cur.path("wind_kph").asDouble(Double.NaN) / 3.6; // convert to m/s
            String desc = cur.path("condition").path("text").asText("");

            CurrentWeatherDto dto = new CurrentWeatherDto();
            dto.setTemperatureC(temp);
            dto.setFeelsLikeC(feels);
            dto.setHumidity(hum == -1 ? null : hum);
            dto.setPressure(pressure);
            dto.setWindSpeedMps(wind);
            dto.setDescription(desc);
            dto.setProvider("weatherapi.com");
            dto.setTimestamp(java.time.Instant.now());
            return Optional.of(dto);
        } catch (Exception ex) {
            log.warn("WeatherAPI current failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<ForecastDto> forecastByQuery(String q, int days) {
        try {
            JsonNode node = webClient.get()
                    .uri(uri -> uri.path("/v1/forecast.json")
                            .queryParam("key", apiKey)
                            .queryParam("q", q)
                            .queryParam("days", days)
                            .build())
                    .retrieve().bodyToMono(JsonNode.class).block();
            if (node == null) return Optional.empty();

            JsonNode forecast = node.path("forecast").path("forecastday");
            List<ForecastDayDto> daysList = new ArrayList<>();
            for (JsonNode d : forecast) {
                ForecastDayDto fd = new ForecastDayDto();
                fd.setDate(java.time.LocalDate.parse(d.path("date").asText()));
                fd.setMinTempC(d.path("day").path("mintemp_c").asDouble());
                fd.setMaxTempC(d.path("day").path("maxtemp_c").asDouble());
                fd.setAvgHumidity(d.path("day").path("avghumidity").asInt());
                fd.setDescription(d.path("day").path("condition").path("text").asText());
                daysList.add(fd);
            }
            ForecastDto out = new ForecastDto();
            out.setLocationName(node.path("location").path("name").asText());
            out.setProvider("weatherapi.com");
            out.setDays(daysList);
            return Optional.of(out);
        } catch (Exception ex) {
            log.warn("WeatherAPI forecast failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}

