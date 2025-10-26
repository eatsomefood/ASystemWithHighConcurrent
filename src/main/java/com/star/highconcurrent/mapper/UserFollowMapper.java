package com.star.highconcurrent.mapper;

import com.star.highconcurrent.model.entity.UserFollow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户关注表 Mapper 接口
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

}
