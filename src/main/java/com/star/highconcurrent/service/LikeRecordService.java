package com.star.highconcurrent.service;

import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.model.dto.LikeRecordDto;
import com.star.highconcurrent.model.entity.LikeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 点赞记录表（支持博客/评论） 服务类
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
public interface LikeRecordService extends IService<LikeRecord> {
    BaseResponse<String> like(LikeRecordDto like);

    BaseResponse<String> unLike(LikeRecordDto record);
}
