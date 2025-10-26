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
import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.BlogContent;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.model.entity.LikeRecord;
import com.star.highconcurrent.model.vo.BlogVo;
import com.star.highconcurrent.service.BlogService;
import com.star.highconcurrent.util.UserContext;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Value("${com.star.retry.retry-size:3}")
    private int retrySize;

    @Value("${com.star.retry.retry-ttl:300}")
    private long retryTtl;

    @Value("${com.star.comment.page-size:10}")
    private int pageSize;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private BlogContentMapper blogContentMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private RedisTemplate<String,Object> template;

    @Resource
    private RedissonClient client;

    @Override
    public BaseResponse getDeclareBlogById(long id) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            return new BaseResponse<>(Code.DATABASE_ERROR);
        } else {
            BlogVo vo = new BlogVo(blog);
            vo.updateUser(UserContext.getUser());
            BlogContent blogContent = blogContentMapper.selectContentByAuthorId(blog.getAuthorId());
            if (blogContent == null) {
                log.error("当前文章内容不存在，请查看: " + blog.getId().toString());
                return new BaseResponse(Code.DATABASE_ERROR);
            }
            vo.updateContent(blogContent);
            Page<Comment> page = new Page<>(0, pageSize);
            QueryWrapper<Comment> queryWrapper = Wrappers.<Comment>query().
                    eq("blog_id", blog.getId()).
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

    /**
     * TODO
     * 当前是本地重试
     * 再更新到mq后，实现死信队列重试
     *
     * @param record
     * @return
     */
    @Override
    public BaseResponse<String> like(LikeRecord record) {
        int currentRetrySize = 0;
        boolean success = false;
        while (currentRetrySize < retrySize) {
            success = doLike(record);
            if (success) {
                return new BaseResponse<>(Code.LIKE_SUCCESS);
            }
            try {
                Thread.sleep(retryTtl);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new BaseResponse<>(Code.LIKE_FAIL);
    }

    public boolean doLike(LikeRecord record) {
        // 先查看当前用户是否点赞过
        // 1.先查redis,没有再查mysql
        String key = PathEnum.USER_LIKE.getPath() +
                record.getTargetType() +
                ":" +
                record.getTargetId();
        // 查看是否点赞
        boolean member = Boolean.TRUE.equals(template.opsForSet().isMember(key, record.getUserId()));
        // 点赞过，返回
        if (member) {
            // 没点赞过，尝试获取分布式锁，然后进行点赞
            return true;
        }else{
            // 尝试查看mysql中是否有点赞记录

            // 没点赞过，获取分布式锁后，开始同步刷入
            RLock lock = client.getLock(key);
            try {
                lock.lock();
                if (lock.isLocked()){
                    // 获取锁成功
                    // 开始写入redis跟mysql中

                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return true;

    }
}
