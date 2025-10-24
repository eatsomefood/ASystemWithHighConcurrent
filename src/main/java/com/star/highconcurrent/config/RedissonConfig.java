package com.star.highconcurrent.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private String port;

    // 读取密码（无密码则为null）
    @Value("${spring.data.redis.password:}")
    private String password;

    // 读取数据库索引
    @Value("${spring.data.redis.database:0}")
    private int database;

    @Bean(destroyMethod = "shutdown",name = "RedissonClient") // 容器销毁时自动关闭客户端
    public RedissonClient redissonClient() {
        Config config = new Config();

        // 配置单节点
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database)
                .setTimeout(3000); // 连接超时时间（与配置文件一致）

        // 若有密码，添加密码配置
        if (password != null && !password.isEmpty()) {
            config.useSingleServer().setPassword(password);
        }

        // 创建并返回 RedissonClient 实例
        return Redisson.create(config);

    }

}
