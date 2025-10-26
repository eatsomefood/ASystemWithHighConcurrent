package com.star.highconcurrent.mapper;

import com.star.highconcurrent.model.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 标签表 Mapper 接口
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

}
