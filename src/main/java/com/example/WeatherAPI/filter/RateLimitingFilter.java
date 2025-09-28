package com.example.WeatherAPI.filter;


import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
// no import for Bucket4j; use Bucket.builder() to avoid deprecated API


@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final long requestsPerMinute;

    public RateLimitingFilter(@Value("${ratelimit.requests.per.minute:60}") long requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.simple(requestsPerMinute, Duration.ofMinutes(1));
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String ip = getClientIp(request);
        Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Too many requests - try again later\"}");
        }
    }
}

