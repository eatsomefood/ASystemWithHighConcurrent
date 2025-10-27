package com.star.highconcurrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.model.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 评论表（支持多级回复） Mapper 接口
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select * from comment where status = 1 and id = #{targetId}")
    Comment selectCommentExist(Long targetId);

}
