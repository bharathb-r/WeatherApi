package com.example.WeatherAPI.config;


import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig {
    @Bean
    public CaffeineCacheManager cacheManager(@Value("${cache.ttl.seconds:300}") long ttlSeconds) {
        CaffeineCacheManager manager = new CaffeineCacheManager("currentWeather", "forecast", "locations");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(5_000));
        return manager;
    }
}

