package com.star.highconcurrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.model.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.cursor.Cursor;

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

    @Select("select like_count from comment where status = 1 and id = #{targetId}")
    int selectLike(Long targetId);

    @Select("select id from comment where status = 1")
    Cursor<Long> getAllBlogIdByCursor();

    @Select("select COUNT(*) from comment")
    int getCount();

    @Update("update comment set like_count = #{count} where id = #{targetId}")
    void updateLikeCount(Long targetId, int count);
}
