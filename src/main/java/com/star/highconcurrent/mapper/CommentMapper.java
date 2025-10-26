package com.star.highconcurrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.highconcurrent.model.entity.Comment;

/**
 * <p>
 * 评论表（支持多级回复） Mapper 接口
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
public interface CommentMapper extends BaseMapper<Comment> {

}
