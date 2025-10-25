package com.star.highconcurrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.highconcurrent.model.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.ResultSetType;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

    //TODO
    @Select("select id from Blog")
    @Options(
            fetchSize = Integer.MIN_VALUE,
            resultSetType = ResultSetType.FORWARD_ONLY
    )
    Cursor<Long> getAllBlogIdByCursor();
}
