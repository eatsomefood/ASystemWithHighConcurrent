package com.star.highconcurrent.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类：管理用户点赞、取消点赞相关的交换机、队列及绑定关系
 * 包含正常队列、死信队列（处理消费失败的消息）的完整配置
 */
@Configuration
public class RabbitMqConfig {

    // ========================= 常量定义：交换机、队列、路由键名称 =========================
    /** 点赞消息交换机（处理正常点赞业务） */
    public static final String USER_LIKE_EXCHANGE = "user.like.exchange";
    /** 取消点赞消息交换机（处理正常取消点赞业务） */
    public static final String USER_UNLIKE_EXCHANGE = "user.unlike.exchange";
    /** 点赞消息队列（接收正常点赞消息） */
    public static final String USER_LIKE_QUEUE = "user.like.queue";
    /** 取消点赞消息队列（接收正常取消点赞消息） */
    public static final String USER_UNLIKE_QUEUE = "user.unlike.queue";
    /** 点赞死信交换机（处理点赞消息消费失败的死信） */
    public static final String USER_LIKE_DEAD_EXCHANGE = "user.dead.like.exchange";
    /** 点赞死信队列（存储点赞消息的死信） */
    public static final String USER_LIKE_DEAD_QUEUE = "user.dead.like.queue";
    /** 取消点赞死信交换机（处理取消点赞消息消费失败的死信） */
    public static final String USER_UNLIKE_DEAD_EXCHANGE = "user.dead.unlike.exchange";
    /** 取消点赞死信队列（存储取消点赞消息的死信） */
    public static final String USER_UNLIKE_DEAD_QUEUE = "user.dead.unlike.queue";
    /** 点赞消息路由键 */
    public static final String USER_LIKE_ROUTING_KEY = "user.like";
    /** 取消点赞消息路由键 */
    public static final String USER_UNLIKE_ROUTING_KEY = "user.unlike";


    // ========================= 点赞相关配置 =========================
    /**
     * 点赞队列：
     * - 持久化队列
     * - 配置死信交换机（消息消费失败后转发至此）
     * - 启用懒加载模式（适合消息量大的场景，减少内存占用）
     */
    @Bean
    public Queue userLikeQueue() {
        return createQueue(USER_LIKE_QUEUE, USER_LIKE_DEAD_EXCHANGE, USER_LIKE_ROUTING_KEY);
    }

    /** 点赞交换机（direct类型，精确匹配路由键） */
    @Bean
    public Exchange userLikeExchange() {
        return createDirectExchange(USER_LIKE_EXCHANGE);
    }

    /** 点赞队列与交换机的绑定（通过点赞路由键） */
    @Bean
    public Binding userLikeBinding() {
        return createBinding(userLikeQueue(), userLikeExchange(), USER_LIKE_ROUTING_KEY);
    }


    // ========================= 取消点赞相关配置 =========================
    /**
     * 取消点赞队列：
     * - 持久化队列
     * - 配置死信交换机（消息消费失败后转发至此）
     * - 启用懒加载模式
     */
    @Bean
    public Queue userUnLikeQueue() {
        return createQueue(USER_UNLIKE_QUEUE, USER_UNLIKE_DEAD_EXCHANGE, USER_UNLIKE_ROUTING_KEY);
    }

    /** 取消点赞交换机（direct类型） */
    @Bean
    public Exchange userUnLikeExchange() {
        return createDirectExchange(USER_UNLIKE_EXCHANGE);
    }

    /** 取消点赞队列与交换机的绑定（通过取消点赞路由键） */
    @Bean
    public Binding userUnLikeBinding() {
        return createBinding(userUnLikeQueue(), userUnLikeExchange(), USER_UNLIKE_ROUTING_KEY);
    }


    // ========================= 点赞死信相关配置 =========================
    /**
     * 点赞死信队列：
     * - 持久化队列（存储消费失败的点赞消息）
     * - 启用懒加载模式
     */
    @Bean
    public Queue userLikeDeadQueue() {
        return createQueue(USER_LIKE_DEAD_QUEUE, null, null); // 死信队列自身可不需要死信配置（按需调整）
    }

    /** 点赞死信交换机（direct类型） */
    @Bean
    public Exchange userLikeDeadExchange() {
        return createDirectExchange(USER_LIKE_DEAD_EXCHANGE);
    }

    /** 点赞死信队列与死信交换机的绑定（通过点赞路由键） */
    @Bean
    public Binding userLikeDeadBinding() {
        return createBinding(userLikeDeadQueue(), userLikeDeadExchange(), USER_LIKE_ROUTING_KEY);
    }


    // ========================= 取消点赞死信相关配置 =========================
    /**
     * 取消点赞死信队列：
     * - 持久化队列（存储消费失败的取消点赞消息）
     * - 启用懒加载模式
     */
    @Bean
    public Queue userUnLikeDeadQueue() {
        return createQueue(USER_UNLIKE_DEAD_QUEUE, null, null); // 死信队列自身可不需要死信配置（按需调整）
    }

    /** 取消点赞死信交换机（direct类型） */
    @Bean
    public Exchange userUnLikeDeadExchange() {
        return createDirectExchange(USER_UNLIKE_DEAD_EXCHANGE);
    }

    /** 取消点赞死信队列与死信交换机的绑定（通过取消点赞路由键） */
    @Bean
    public Binding userUnLikeDeadBinding() {
        return createBinding(userUnLikeDeadQueue(), userUnLikeDeadExchange(), USER_UNLIKE_ROUTING_KEY);
    }


    // ========================= 公共方法：抽取重复配置逻辑 =========================
    /**
     * 创建持久化队列
     * @param queueName 队列名称
     * @param deadLetterExchange 死信交换机（可为null，即不配置死信）
     * @param deadLetterRoutingKey 死信路由键（可为null）
     * @return 配置后的队列
     */
    private Queue createQueue(String queueName, String deadLetterExchange, String deadLetterRoutingKey) {
        QueueBuilder queueBuilder = QueueBuilder.durable(queueName)
                .lazy(); // 启用懒加载（消息优先存储在磁盘，减少内存占用）

        // 若配置了死信交换机，则添加死信参数
        if (deadLetterExchange != null && deadLetterRoutingKey != null) {
            queueBuilder.deadLetterExchange(deadLetterExchange)
                    .deadLetterRoutingKey(deadLetterRoutingKey);
        }
        return queueBuilder.build();
    }

    /**
     * 创建持久化的direct交换机
     * @param exchangeName 交换机名称
     * @return 配置后的交换机
     */
    private Exchange createDirectExchange(String exchangeName) {
        return ExchangeBuilder.directExchange(exchangeName)
                .durable(true) // 持久化交换机（重启后不丢失）
                .build();
    }

    /**
     * 创建队列与交换机的绑定关系
     * @param queue 队列
     * @param exchange 交换机
     * @param routingKey 路由键
     * @return 绑定关系
     */
    private Binding createBinding(Queue queue, Exchange exchange, String routingKey) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(routingKey)
                .noargs();
    }
}