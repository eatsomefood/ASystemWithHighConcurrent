package com.star.highconcurrent.mq;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMqTest {

    @Resource
    private RabbitTemplate template;

    @Test
    public void mqTest(){
        String content = "user:like:51";
        template.convertAndSend("like.51",content);
    }

}
