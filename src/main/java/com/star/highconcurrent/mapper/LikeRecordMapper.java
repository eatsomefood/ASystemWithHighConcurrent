package com.star.highconcurrent.mapper;

import com.star.highconcurrent.model.entity.LikeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 点赞记录表（支持博客/评论） Mapper 接口
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

}
