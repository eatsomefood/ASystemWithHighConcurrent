package com.star.highconcurrent.service.impl;

import com.star.highconcurrent.model.entity.BlogTag;
import com.star.highconcurrent.mapper.BlogTagMapper;
import com.star.highconcurrent.service.BlogTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 博客-标签关联表（多对多） 服务实现类
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Service
public class BlogTagServiceImpl extends ServiceImpl<BlogTagMapper, BlogTag> implements BlogTagService {

}
