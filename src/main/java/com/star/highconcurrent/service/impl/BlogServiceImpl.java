package com.star.highconcurrent.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.mapper.BlogMapper;
import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.service.BlogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Resource
    private BlogMapper mapper;

    @Override
    public BaseResponse getBlogById(long id) {
        Blog blog = mapper.selectById(id);
        if (blog == null){
            return new BaseResponse<>(Code.DATABASE_ERROR);
        }else {
            return new BaseResponse<>(Code.OK,blog);
        }
    }
}
