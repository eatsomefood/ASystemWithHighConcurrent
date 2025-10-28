package com.star.highconcurrent.service.impl;

import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.common.PathEnum;
import com.star.highconcurrent.mapper.BlogMapper;
import com.star.highconcurrent.mapper.CommentMapper;
import com.star.highconcurrent.mapper.UserMapper;
import com.star.highconcurrent.model.dto.LikeRecordDto;
import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.model.entity.LikeRecord;
import com.star.highconcurrent.mapper.LikeRecordMapper;
import com.star.highconcurrent.service.LikeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 点赞记录表（支持博客/评论） 服务实现类
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Service
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeRecordService {

    private final String PREFIX = "like:";

    @Value("${com.star.retry.retry-size:3}")
    private int retrySize;

    @Value("${com.star.retry.retry-ttl:300}")
    private long retryTtl;

    @Resource
    private RedisTemplate<String, Object> template;

    @Resource
    private RedissonClient client;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private LikeRecordMapper mapper;

    @Resource
    private TransactionTemplate transactionTemplate;

    private static final DefaultRedisScript<Long> LIKE_UPDATE_SCRIPT;

    static {
        LIKE_UPDATE_SCRIPT = new DefaultRedisScript<>();
        LIKE_UPDATE_SCRIPT.setLocation(new ClassPathResource("script/doLike.lua"));
        LIKE_UPDATE_SCRIPT.setResultType(Long.class);
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
    public BaseResponse<String> like(LikeRecordDto record) {
        int currentRetrySize = 0;
        boolean success = false;
        while (currentRetrySize < retrySize) {
            success = doLike(record);
            if (success) {
                return new BaseResponse<>(Code.LIKE_SUCCESS);
            }
            try {
                Thread.sleep(retryTtl);
                currentRetrySize++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new BaseResponse<>(Code.LIKE_FAIL);
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
    public BaseResponse<String> unLike(LikeRecordDto record) {
        int currentRetrySize = 0;
        boolean success = false;
        while (currentRetrySize < retrySize) {
            success = doUnLike(record);
            if (success) {
                return new BaseResponse<>(Code.UNLIKE_SUCCESS);
            }
            try {
                Thread.sleep(retryTtl);
                currentRetrySize++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new BaseResponse<>(Code.LIKE_FAIL);
    }

    public boolean doUnLike(LikeRecordDto record) {
        String key;
        String blogOrCommentLikeCountKey;
        String userLikeBlogOrCommentKey = PathEnum.USER_LIKE.getPath() + record.getUserId();
        if (record.getTargetType() == 1) {
            key = PathEnum.BLOG_LIKE.getPath() + record.getTargetId();
            blogOrCommentLikeCountKey = PathEnum.BLOG_LIKE_COUNT.getPath() + record.getTargetId();
        } else {
            key = PathEnum.COMMENT_LIKE.getPath() + record.getTargetId();
            blogOrCommentLikeCountKey = PathEnum.COMMENT_LIKE_COUNT.getPath() + record.getTargetId();
        }
        // 查看是否点赞
        boolean member = Boolean.TRUE.equals(template.opsForSet().isMember(key, record.getUserId()));
        if (!member) {
            // 没点赞过，看看mysql有没有记录
            LikeRecord currentDBRecord = mapper.selectLikeByRecord(record);
            if (currentDBRecord != null) {
                // 点赞成功，但是没有成功刷入redis，尝试刷入后返回
                mapper.logicDelete(currentDBRecord);
            }
            return true;
        } else {
            try {
                // 开始写入redis跟mysql中
                LikeRecord currentDBRecord = mapper.selectLikeByRecord(record);
                try {
                    int likeCount;
                    if (record.getStatus() == 1) {
                        likeCount = blogMapper.selectLike(record.getTargetId());
                    } else {
                        likeCount = commentMapper.selectLike(record.getTargetId());
                    }
                    List<String> keys = List.of(key, blogOrCommentLikeCountKey, userLikeBlogOrCommentKey);
                    Long[] arr = new Long[]{record.getUserId(), record.getTargetId(), 0L, (long) likeCount};
                    //lua脚本插入点赞
                    Long execute = template.execute(LIKE_UPDATE_SCRIPT, keys, arr);
                    if (execute == 0) {
                        // 脚本执行失败
                        return false;
                    } else {
                        // 继续执行mysql逻辑
                        mapper.logicDelete(currentDBRecord);
                    }
                    return true;
                } catch (Exception e) {
                    log.error("当前点赞执行失败:{}" + "/n" + record.toString(), e);
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return false;
            }
        }
    }

    public boolean doLike(LikeRecordDto record) {
        // 先查看当前用户是否点赞过
        // 1.先查redis,没有再查mysql
        String key;
        String blogOrCommentLikeCountKey;
        String userLikeBlogOrCommentKey = PathEnum.USER_LIKE.getPath() + record.getUserId();
        if (record.getTargetType() == 1) {
            key = PathEnum.BLOG_LIKE.getPath() + record.getTargetId();
            blogOrCommentLikeCountKey = PathEnum.BLOG_LIKE_COUNT.getPath() + record.getTargetId();
        } else {
            key = PathEnum.COMMENT_LIKE.getPath() + record.getTargetId();
            blogOrCommentLikeCountKey = PathEnum.COMMENT_LIKE_COUNT.getPath() + record.getTargetId();
        }
        // 查看是否点赞
        // 点赞过，返回
        // 尝试查看mysql中是否有点赞记录
        try {
            // 开始写入redis跟mysql中
            LikeRecord currentDBRecord = mapper.selectLikeByRecord(record);
            try {
                int likeCount;
                if (record.getStatus() == 1) {
                    likeCount = blogMapper.selectLike(record.getTargetId());
                } else {
                    likeCount = commentMapper.selectLike(record.getTargetId());
                }
                List<String> keys = List.of(key, blogOrCommentLikeCountKey, userLikeBlogOrCommentKey);
                Long[] arr = new Long[]{record.getUserId(), record.getTargetId(), 1L, (long) likeCount};
                //lua脚本插入点赞
                Long execute = template.execute(LIKE_UPDATE_SCRIPT, keys, arr);
                if (execute == 0) {
                    // 脚本执行失败
                    return false;
                } else {
                    // 继续执行mysql逻辑
                    if (currentDBRecord != null) {
                        return true;
                    } else {
                        mapper.insertByDto(record);
                    }
                }
                return true;
            } catch (Exception e) {
                log.error("当前点赞执行失败:{}" + "/n" + record.toString(), e);
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
