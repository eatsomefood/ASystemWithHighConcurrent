package com.star.highconcurrent.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CaffeineConfig {

    @Bean
    public CacheManager getCaffeineManager(){
        Caffeine<Object, Object> blogCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .expireAfterWrite(Duration.ofHours(2))
                .recordStats();

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 存入manager中
        String cacheSpec = "blogCache=maximumSize=100,expireAfterAccess=1h,expireAfterWrite=2h;";
        cacheManager.setCacheSpecification(cacheSpec);
        cacheManager.setCaffeine(
                Caffeine.newBuilder().
                        maximumSize(50).
                        expireAfterAccess(1,TimeUnit.HOURS)
                        .expireAfterWrite(2,TimeUnit.HOURS)
        );
        return cacheManager;
    }

}
