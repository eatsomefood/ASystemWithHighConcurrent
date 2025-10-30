package com.star.highconcurrent.asyncListener;

import com.rabbitmq.client.Channel;
import com.star.highconcurrent.config.RabbitMqConfig;
import com.star.highconcurrent.mapper.LikeRecordMapper;
import com.star.highconcurrent.model.dto.LikeRecordDto;
import com.star.highconcurrent.model.entity.LikeRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class LikeListener {

    @Resource
    private LikeRecordMapper likeRecordMapper;

    @RabbitListener(queues = RabbitMqConfig.USER_LIKE_QUEUE,ackMode = "MANUAL")
    public void handleLikeMessage(LikeRecordDto message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, IOException {
        try {
            log.debug("正在消费消息:{}",message);
            LikeRecord likeRecord = LikeRecord.transferDtoToEntity(message);
            if (message.getStatus() == 1) {
                // 点赞：插入或更新状态为1
                likeRecordMapper.insertByEntity(likeRecord);
            }
            // 手动确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 失败重试（超过最大次数后进入死信队列）
            channel.basicNack(tag, false, false);
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMqConfig.USER_UNLIKE_QUEUE,ackMode = "MANUAL")
    public void handleUnLikeMessage(LikeRecordDto message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException, IOException {
        try {
            log.debug("正在消费消息:{}",message);
            LikeRecord likeRecord = LikeRecord.transferDtoToEntity(message);
            if (message.getStatus() == 0) {
                // 点赞：插入或更新状态为1
                likeRecordMapper.logicDelete(likeRecord);
                channel.basicAck(tag, false);
            }else {
                log.error("当前消息格式错误，请辨别,{}",message);
                channel.basicReject(tag,false);
            }
            // 手动确认消息
        } catch (Exception e) {
            // 失败重试（超过最大次数后进入死信队列）
            channel.basicNack(tag, false, false);
            throw new RuntimeException(e);
        }
    }
}




