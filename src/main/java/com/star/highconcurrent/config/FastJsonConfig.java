package com.star.highconcurrent.config;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FastJsonConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 1. 清除默认的消息转换器（避免Jackson和FastJson同时存在）
        converters.clear();

        // 2. 创建FastJson消息转换器
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        // 3. 配置FastJson的序列化规则
        com.alibaba.fastjson.support.config.FastJsonConfig fastJsonConfig = new com.alibaba.fastjson.support.config.FastJsonConfig();
        // 设置默认编码（UTF-8）
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        // 配置序列化特性（按需添加）
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat, // 格式化JSON（开发环境用，生产环境可关闭）
                SerializerFeature.WriteMapNullValue, // 输出null值字段（默认不输出）
                SerializerFeature.WriteNullStringAsEmpty, // null字符串输出为""
                SerializerFeature.WriteNullNumberAsZero, // null数字输出为0
                SerializerFeature.WriteNullListAsEmpty, // null列表输出为[]
                SerializerFeature.WriteNullBooleanAsFalse, // null布尔值输出为false
                SerializerFeature.DisableCircularReferenceDetect // 禁用循环引用检测（避免$ref）
        );

        // 4. 配置支持的媒体类型（JSON）
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8); // 兼容旧版UTF-8声明
        fastConverter.setSupportedMediaTypes(supportedMediaTypes);

        // 5. 将配置绑定到转换器
        fastConverter.setFastJsonConfig(fastJsonConfig);

        // 6. 添加FastJson转换器到列表（作为默认转换器）
        converters.add(fastConverter);
    }
}