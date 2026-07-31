package com.musiccatalog.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    public static final String SEARCH_CACHE = "itunesSearchCache";
    public static final String ANALYTICS_CACHE = "analyticsCache";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(SEARCH_CACHE, ANALYTICS_CACHE);
    }
}
