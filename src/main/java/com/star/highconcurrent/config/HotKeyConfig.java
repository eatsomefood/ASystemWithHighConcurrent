package com.star.highconcurrent.config;

import com.jd.platform.hotkey.client.ClientStarter;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hotkey")
@Data
public class HotKeyConfig {

    /**
     * Etcd 服务器完整地址
     */
    @Value("${hotkey.etcd-server}")
    private String etcdServer;

    /**
     * 应用名称
     */
    @Value("${hotkey.app-name}")
    private String appName = "star";

    /**
     * 本地缓存最大数量
     */
    @Value("${hotkey.caffeine-size}")
    private int caffeineSize = 10000;

    /**
     * 批量推送 key 的间隔时间
     */
    @Value("${hotkey.push-period}")
    private long pushPeriod = 1000L;

    /**
     * 初始化 hotkey
     */
    @Bean
    public ClientStarter initHotkey() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName)
                .setCaffeineSize(caffeineSize)
                .setPushPeriod(pushPeriod)
                .setEtcdServer(etcdServer)
                .build();
        starter.startPipeline();
        return starter;
    }

}
