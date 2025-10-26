package com.star.highconcurrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.highconcurrent.model.entity.BlogContent;
import com.star.highconcurrent.service.BlogContentService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BlogContentMapper extends BaseMapper<BlogContent> {

    @Select("select * from blog_content where is_delete = 1 and blog_id = #{id}")
    BlogContent selectContentByAuthorId(Long id);

}
