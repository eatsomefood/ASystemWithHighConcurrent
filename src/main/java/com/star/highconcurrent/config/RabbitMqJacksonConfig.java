package com.star.highconcurrent.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMqJacksonConfig {

    /**
     * 定义 Jackson 消息转换器（核心）
     * 负责将 Java 对象序列化为 JSON，或将 JSON 反序列化为 Java 对象
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // 自定义 ObjectMapper（可选，用于配置序列化规则，如日期格式、忽略未知字段等）
        ObjectMapper objectMapper = new ObjectMapper();
        // 解决 LocalDateTime 等 JDK8 时间类型序列化问题
        objectMapper.registerModule(new JavaTimeModule());
        // 忽略未知字段（避免消费端对象新增字段时反序列化失败）
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        // 创建 Jackson 消息转换器并绑定自定义 ObjectMapper
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * 配置 RabbitTemplate，使用 Jackson 序列化器
     * 确保生产者发送消息时使用 JSON 序列化
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 设置消息转换器（核心：替换默认的 JDK 序列化）
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }

    /**
     * 配置消费者监听容器工厂，使用 Jackson 反序列化
     * 确保消费者接收消息时能正确将 JSON 反序列化为 Java 对象
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        // 消费者同样使用 Jackson 转换器（否则会反序列化失败）
        factory.setMessageConverter(jackson2JsonMessageConverter);
        return factory;
    }
}