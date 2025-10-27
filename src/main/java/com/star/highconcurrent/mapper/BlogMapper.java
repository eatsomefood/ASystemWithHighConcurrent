package com.star.highconcurrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.LikeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.ResultSetType;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

    @Select("select id from Blog")
    @Options(
            fetchSize = Integer.MIN_VALUE,
            resultSetType = ResultSetType.FORWARD_ONLY
    )
    Cursor<Long> getAllBlogIdByCursor();

    @Select("select * from blog where status = 1 and id = #{targetId}")
    Blog selectBlogExist(Long targetId);

    @Select("select * from blog where status = 1 and id = #{id}")
    Blog selectSuccessById(long id);

    @Select("select like_count from blog where status = 1 and id = #{targetId}")
    int selectLike(Long targetId);

    @Select("select COUNT(*) from blog")
    int getCount();
}
