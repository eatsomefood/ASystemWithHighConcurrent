package com.star.highconcurrent.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.highconcurrent.mapper.CommentMapper;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论表（支持多级回复） 服务实现类
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
