package com.star.highconcurrent.mapper;

import com.star.highconcurrent.model.entity.BlogTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 博客-标签关联表（多对多） Mapper 接口
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTag> {

}
