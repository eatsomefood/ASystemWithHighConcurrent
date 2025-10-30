package com.star.highconcurrent.bloomFilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BlackIpBloomFilter {

    @Value("${com.star.bloom-filter.blog.miss-precent:0.1}")
    private double missPrecent;

    private BitMapBloomFilter filter;


    // 初始化布隆过滤器
    public void addDataToFilter(List<String> list){
        for (String s : list) {
            filter.add(s);
        }
    }

    public void addDataToFilter(String configInfo) {
        if (StrUtil.isBlank(configInfo)) {
            configInfo = "{}";
        }
        // 解析 yaml 文件
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(configInfo, Map.class);
        // 获取 IP 黑名单
        List<String> blackIpList = (List<String>) map.get("blackIpList");
        // 加锁防止并发
        synchronized (BlackIpBloomFilter.class) {
            if (CollUtil.isNotEmpty(blackIpList)) {
                // 注意构造参数的设置
                for (String blackIp : blackIpList) {
                    filter.add(blackIp);
                }
            }
        }
    }

    public void addData(String ip) {
        filter.add(ip);
    }

    public boolean isValid(String ip) {
        return filter.contains(ip);
    }
}
