package com.star.highconcurrent.controller;


import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.config.RabbitMqConfig;
import com.star.highconcurrent.model.dto.LikeRecordDto;
import com.star.highconcurrent.service.LikeRecordService;
import com.star.highconcurrent.bloomFilter.BlogBloomFilter;
import com.star.highconcurrent.bloomFilter.CommentBloomFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 点赞记录表（支持博客/评论） 前端控制器
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Slf4j
@Tag(name = "点赞记录")
@RestController
@RequestMapping("/like-record")
public class LikeRecordController {

    @Resource
    private LikeRecordService service;

    @Resource
    private BlogBloomFilter blogBloomFilter;

    @Resource
    private CommentBloomFilter commentBloomFilter;

    @Operation(description = "点赞")
    @PostMapping("/like")
    public BaseResponse<String> like(@RequestBody LikeRecordDto record) {
        if (
                record == null
                        || (record.getTargetType() == 1 && !blogBloomFilter.isValid(record.getTargetId()))
                        || (record.getTargetType() == 2 && !commentBloomFilter.isValid(record.getTargetId()))) {
            return new BaseResponse(Code.PARAM_ERROR);
        } else {
            return service.like(record);
        }
    }

    @Operation(description = "取消点赞")
    @PostMapping("/unlike")
    public BaseResponse<String> unLike(@RequestBody LikeRecordDto record) {
        if (
                record == null
                        || (record.getTargetType() == 1 && !blogBloomFilter.isValid(record.getTargetId()))
                        || (record.getTargetType() == 2 && !commentBloomFilter.isValid(record.getTargetId()))) {
            return new BaseResponse(Code.PARAM_ERROR);
        } else {
            return service.unLike(record);
        }
    }

    @Operation(description = "查看死信队列消息总数")
    @GetMapping("/dead/{queue}")
    public BaseResponse<String> getDeadMessageCount(@PathVariable("queue") String type) {
        switch (type) {
            case "like":
                return service.getMessageCount(RabbitMqConfig.USER_LIKE_DEAD_QUEUE);
            case "unlike":
                return service.getMessageCount(RabbitMqConfig.USER_UNLIKE_DEAD_QUEUE);
            default:
                log.error("死信队列消息统计参数错误:{}", type);
                return new BaseResponse<>(Code.PARAM_ERROR);
        }
    }

    @Operation(description = "获取死信队列信息")
    @GetMapping("/dead/single/{queue}")
    public BaseResponse<List<LikeRecordDto>> getDeadMessage(@PathVariable("queue") String type, int size, boolean keepInQueue) {
        switch (type) {
            case "like":
                return service.getMessage(RabbitMqConfig.USER_LIKE_DEAD_QUEUE, size, keepInQueue);
            case "unlike":
                return service.getMessage(RabbitMqConfig.USER_UNLIKE_DEAD_QUEUE, size, keepInQueue);
            default:
                log.error("死信队列消息统计参数错误:{}", type);
                return new BaseResponse<>(Code.PARAM_ERROR);
        }
    }


}
