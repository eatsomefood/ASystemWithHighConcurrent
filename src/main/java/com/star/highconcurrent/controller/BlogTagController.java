package com.star.highconcurrent.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 博客-标签关联表（多对多） 前端控制器
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Tag(name = "博客标签关联")
@RestController
@RequestMapping("/blog-tag")
public class BlogTagController {

}
