package com.star.highconcurrent.service.impl;

import com.star.highconcurrent.model.entity.UserFollow;
import com.star.highconcurrent.mapper.UserFollowMapper;
import com.star.highconcurrent.service.UserFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户关注表 服务实现类
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Service
public class UserFollowServiceImpl extends ServiceImpl<UserFollowMapper, UserFollow> implements UserFollowService {

}
