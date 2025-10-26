package com.star.highconcurrent.controller;


import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.model.dto.LikeRecordDto;
import com.star.highconcurrent.model.entity.LikeRecord;
import com.star.highconcurrent.service.LikeRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 点赞记录表（支持博客/评论） 前端控制器
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Tag(name = "点赞记录")
@RestController
@RequestMapping("/like-record")
public class LikeRecordController {

    @Resource
    private LikeRecordService service;

    @Operation(description = "点赞")
    @PostMapping("/like")
    public BaseResponse<String> like(@RequestBody LikeRecordDto record) {
        if(record == null){
            return new BaseResponse(Code.PARAM_ERROR);
        }else {
            return service.like(record);
        }
    }
}
