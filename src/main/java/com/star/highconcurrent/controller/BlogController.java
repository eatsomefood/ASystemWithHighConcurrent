package com.star.highconcurrent.controller;

import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.Page;
import com.star.highconcurrent.service.BlogService;
import com.star.highconcurrent.util.BlogBloomFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/blog")
@Tag(name = "博客管理")
@Slf4j
@RestController
public class BlogController {

    @Resource
    private BlogService service;

    @Resource
    private BlogBloomFilter filter;

    // 查询博客
    @Operation()
    @GetMapping("/{id}")
    public BaseResponse getDeclareBlogById(@PathVariable("id") long id) {
        // 先查健壮性和布隆过滤器
        if (id <= 0 || !filter.isValid(id)) {
            return new BaseResponse<>(Code.PARAM_ERROR);
        }
        // 查业务
        return service.getDeclareBlogById(id);
    }

    @Operation(description = "分页查询")
    @PostMapping("/list")
    public BaseResponse<List<Blog>> getBlogList(@RequestBody Page page) {
        return service.getListByPage(page);
    }

}
