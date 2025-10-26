package com.star.highconcurrent.service.impl;

import com.star.highconcurrent.model.entity.LikeRecord;
import com.star.highconcurrent.mapper.LikeRecordMapper;
import com.star.highconcurrent.service.LikeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 点赞记录表（支持博客/评论） 服务实现类
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Service
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeRecordService {

}
