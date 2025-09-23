package com.portfolio.management.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // Default cache configuration
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats());

        // Specific cache configurations
        cacheManager.registerCustomCache("portfolios",
                Caffeine.newBuilder()
                        .maximumSize(500)
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .build());

        cacheManager.registerCustomCache("holdings",
                Caffeine.newBuilder()
                        .maximumSize(2000)
                        .expireAfterWrite(2, TimeUnit.MINUTES)
                        .build());

        cacheManager.registerCustomCache("users",
                Caffeine.newBuilder()
                        .maximumSize(200)
                        .expireAfterWrite(15, TimeUnit.MINUTES)
                        .build());

        return cacheManager;
    }
}