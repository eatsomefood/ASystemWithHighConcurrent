package com.star.highconcurrent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.model.entity.Blog;

public interface BlogService extends IService<Blog> {

    BaseResponse<String> getBlogById(long id);
}
