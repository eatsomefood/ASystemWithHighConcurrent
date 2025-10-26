package com.star.highconcurrent.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 评论表（支持多级回复） 前端控制器
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Tag(name = "评论")
@RestController
@RequestMapping("/comment")
public class CommentController {

}
