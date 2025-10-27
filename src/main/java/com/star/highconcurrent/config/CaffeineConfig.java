package com.star.highconcurrent.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Policy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CaffeineConfig {

    @Value("${com.star.blog.cache.local-size:50}")
    private int cacheSize;

    @Value("${com.star.blog.cache.access-time:1}")
    private int accessTime;

    @Value("${com.star.blog.cache.write-time:2}")
    private int writeTime;

    @Bean(name = "blogCache")
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.registerCustomCache("blogCache", Caffeine.newBuilder().
                maximumSize(cacheSize).
                expireAfterAccess(accessTime, TimeUnit.HOURS)
                .expireAfterWrite(writeTime, TimeUnit.HOURS).build());

        // 验证配置（获取blogCache的实际策略）
        Cache blogCache = cacheManager.getCache("blogCache");
        CaffeineCache caffeineCache = (CaffeineCache) blogCache;
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();

//        // 打印最大容量（应输出50）
//        System.out.println(nativeCache.policy().toString());
//        // 打印访问后过期时间（应输出3600秒 = 1小时）
//        System.out.println("expireAfterAccess: " + nativeCache.policy().expireAfterAccess().map(Policy.FixedExpiration::getExpiresAfter).map(Duration::getSeconds).orElse(-1L));
//        // 打印写入后过期时间（应输出7200秒 = 2小时）
//        System.out.println("expireAfterWrite: " + nativeCache.policy().expireAfterWrite().map(Policy.FixedExpiration::getExpiresAfter).map(Duration::getSeconds).orElse(-1L));

        return cacheManager;
    }

}
