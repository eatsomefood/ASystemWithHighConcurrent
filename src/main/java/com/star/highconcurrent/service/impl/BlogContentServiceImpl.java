package com.star.highconcurrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.highconcurrent.mapper.BlogContentMapper;
import com.star.highconcurrent.model.entity.BlogContent;
import com.star.highconcurrent.service.BlogContentService;
import org.springframework.stereotype.Service;

@Service
public class BlogContentServiceImpl extends ServiceImpl<BlogContentMapper, BlogContent> implements BlogContentService {
}
