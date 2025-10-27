package com.star.highconcurrent.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Slf4j
@Configuration
public class RedisConfig{

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // 读取密码（无密码则为null）
    @Value("${spring.data.redis.password:}")
    private String password;

    // 读取数据库索引
    @Value("${spring.data.redis.database:0}")
    private int database;

    @Bean
    public RedisConnectionFactory getFactory(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(database);
        configuration.setPassword(password);
        configuration.setPort(port);
        configuration.setHostName(host);

        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .build();

        return new LettuceConnectionFactory(configuration,clientConfiguration);
    }

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) throws Exception{
        log.info("开始创建Redis模板对象");
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // 配置GenericJackson2JsonRedisSerializer，注册JavaTimeModule
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())  // 关键：注册Java 8日期模块
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)  // 禁用时间戳格式
        );

        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);  // 哈希值序列化也使用该配置
        // 其他配置（key序列化器等）
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
