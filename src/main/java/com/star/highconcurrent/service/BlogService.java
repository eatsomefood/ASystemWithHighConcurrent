package com.star.highconcurrent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.Page;

import java.util.List;

public interface BlogService extends IService<Blog> {

    BaseResponse<String> getDeclareBlogById(long id);

    BaseResponse<List<Blog>> getListByPage(Page page);
}
