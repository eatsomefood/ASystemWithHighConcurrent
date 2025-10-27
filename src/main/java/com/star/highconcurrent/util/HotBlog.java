package com.star.highconcurrent.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.common.PathEnum;
import com.star.highconcurrent.mapper.BlogContentMapper;
import com.star.highconcurrent.mapper.BlogMapper;
import com.star.highconcurrent.mapper.CommentMapper;
import com.star.highconcurrent.mapper.UserMapper;
import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.BlogContent;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.model.entity.User;
import com.star.highconcurrent.model.vo.BlogVo;
import com.star.highconcurrent.model.vo.UserVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 实现服务启动时，自动同步最热的50条数据
 */
@Component
@Slf4j
public class HotBlog implements CommandLineRunner {

    @Resource
    private RedisTemplate<String, Object> template;

    @Value("${com.star.blog.cache.redis-size:50}")
    private int cacheSize;

    @Value("${com.star.comment.page-size:5}")
    private int pageSize;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private BlogContentMapper blogContentMapper;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private CacheManager cacheManager;

    public void getHotBlog() {
        // 从数据库中获取到热度排行前100的博客
        Page page = new Page(0, cacheSize);
        // 构建wrapper对象
        QueryWrapper<Blog> queryWrapper = Wrappers.<Blog>query()
                .eq("status",1)
                .orderByDesc("view_count");
        Page selectPage = blogMapper.selectPage(page, queryWrapper);
        // 查询结果
        List<Blog> records = page.getRecords();
        // 热度前50的博客加载到caffeine中
        for (int i = 0; i < Math.min(records.size(), 50); i++) {
            // 拿到博客数据后，查询出博客的对应信息
            Blog blog = records.get(i);
            BlogVo vo = getBlogVo(blog);
            Cache blogCache = cacheManager.getCache("blogCache");
            if (blogCache != null) {
                blogCache.put(blog.getId(),vo);
            }
        }
        // 剩下的加载到redis中
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
        for (Blog record : records) {
            double score = record.getViewCount();
            ZSetOperations.TypedTuple<Object> typedTuple = new DefaultTypedTuple<>(record.getId(), score);
            tuples.add(typedTuple);
            BlogVo blogVo = getBlogVo(record);
            // 尝试加载blogVo到redis中
            template.opsForHash().putAll(PathEnum.GET_BLOG.getPath() + record.getId(),blogVo.getBlogMap());
        }
        // 尝试加载到redis中
        template.opsForZSet().add(PathEnum.HOT_BLOG.getPath(), tuples);
        log.info("初始化redis , Blog排行榜完成");
    }

    private BlogVo getBlogVo(Blog blog) {
        BlogVo vo = new BlogVo(blog);
        UserVo user = userMapper.selectVoById(blog.getAuthorId());
        vo.updateUser(user);
        BlogContent blogContent = blogContentMapper.selectContentByAuthorId(blog.getAuthorId());
        if (blogContent == null) {
            log.error("当前文章内容不存在，请查看: " + blog.getId().toString());
        }
        vo.updateContent(blogContent);
        Page<Comment> page1 = new Page<>(0, pageSize);
        QueryWrapper<Comment> queryWrapper1 = Wrappers.<Comment>query().
                eq("blog_id", blog.getId()).
                eq("parent_id",0).
                eq("status",1).
                orderByDesc("created_at");
        Page<Comment> commentPage = commentMapper.selectPage(page1, queryWrapper1);
        List<Comment> records1 = commentPage.getRecords();
        vo.setComments(records1);
        return vo;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("正在同步热点博客数据");
        getHotBlog();
    }
}
