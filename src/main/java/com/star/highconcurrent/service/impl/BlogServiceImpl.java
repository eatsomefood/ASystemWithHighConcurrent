package com.star.highconcurrent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.common.PathEnum;
import com.star.highconcurrent.mapper.BlogContentMapper;
import com.star.highconcurrent.mapper.BlogMapper;
import com.star.highconcurrent.mapper.CommentMapper;
import com.star.highconcurrent.mapper.UserMapper;
import com.star.highconcurrent.model.entity.*;
import com.star.highconcurrent.model.vo.BlogVo;
import com.star.highconcurrent.model.vo.UserVo;
import com.star.highconcurrent.service.BlogService;
import com.star.highconcurrent.util.UserContext;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Value("${com.star.comment.page-size:10}")
    private int pageSize;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private BlogContentMapper blogContentMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String,Object> template;

    private final Cache blogCache;

    public BlogServiceImpl(CacheManager manager){
        this.blogCache = manager.getCache("blogCache");
    }

    /**
     * TODO 在完成正常功能之后，尝试加入本地缓存
     * 1: 先从redis中尝试查询当前博客数据
     * 2：查询到，聚合数据并返回
     * 3：如果没有查询到，这时候尝试查询mysql,如果当前博客存在，聚合数据，并异步刷入redis中
     * 4:同时需要做缓存击穿，雪崩等场景考虑
     * @param id
     * @return
     */
    @Override
    public BaseResponse getDeclareBlogById(long id) {
        // 尝试从本地缓存中获取
        blogCache.
        // 先尝试从redis中获取当前博客
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            return new BaseResponse<>(Code.DATABASE_ERROR);
        } else {
            BlogVo vo = new BlogVo(blog);
            UserVo user = userMapper.selectVoById(blog.getAuthorId());
            vo.updateUser(user);
            BlogContent blogContent = blogContentMapper.selectContentByAuthorId(blog.getAuthorId());
            if (blogContent == null) {
                log.error("当前文章内容不存在，请查看: " + blog.getId().toString());
                return new BaseResponse(Code.DATABASE_ERROR);
            }
            vo.updateContent(blogContent);
            Page<Comment> page = new Page<>(0, pageSize);
            QueryWrapper<Comment> queryWrapper = Wrappers.<Comment>query().
                    eq("blog_id", blog.getId()).
                    eq("status",1).
                    orderByDesc("created_at");
            Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
            List<Comment> records = commentPage.getRecords();
            vo.setComments(records);
            return new BaseResponse<>(Code.OK, vo);
        }
    }

    @Override
    public BaseResponse<List<Blog>> getListByPage(com.star.highconcurrent.model.entity.Page page) {
        List<Blog> records = null;
        try {
            Page<Blog> wrapperPage = new Page<Blog>();
            if (page == null) {
                wrapperPage.setCurrent(1);
                wrapperPage.setSize(pageSize);
            } else {
                int size = page.getPageSize();
                int pageNum = page.getPageNum();
                if (size <= 0) {
                    wrapperPage.setSize(pageSize);
                } else {
                    wrapperPage.setCurrent(size);
                }
                if (pageNum <= 0) {
                    wrapperPage.setCurrent(1);
                } else {
                    wrapperPage.setCurrent(pageNum);
                }
            }
            // 构建page对象后，开始构建queryWrapper对象
            QueryWrapper<Blog> listQueryWrapper = Wrappers.<Blog>query()
                    .eq("status", 2)
                    .orderByDesc("created_at");
            Page<Blog> selectPage = blogMapper.selectPage(wrapperPage, listQueryWrapper);
            records = selectPage.getRecords();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(Code.ERROR);
        }
        return new BaseResponse<>(Code.OK, records);
    }


}
