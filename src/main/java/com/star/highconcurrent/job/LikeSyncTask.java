package com.star.highconcurrent.job;

import com.star.highconcurrent.common.PathEnum;
import com.star.highconcurrent.mapper.BlogMapper;
import com.star.highconcurrent.mapper.CommentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Set;

@Slf4j
@Component
public class LikeSyncTask {

    @Resource
    private RedisTemplate<String,Object> template;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private CommentMapper commentMapper;

    private static final String BLOG_LIKE_COUNT_PREFIX = PathEnum.BLOG_LIKE_COUNT.getPath();
    private static final String COMMENT_LIKE_COUNT_PREFIX = PathEnum.COMMENT_LIKE_COUNT.getPath();

    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncLikeCountToDb(){
        log.info("正在同步当前点赞数,当前时间：{}",new Date());
        syncTargetLikeCount(BLOG_LIKE_COUNT_PREFIX,true);
        syncTargetLikeCount(COMMENT_LIKE_COUNT_PREFIX,false);
    }

    /**
     * 同步指定类型（博客/评论）的点赞数
     * @param redisKeyPrefix Redis计数键前缀
     * @param isBlog 是否为博客（true-博客，false-评论）
     */
    private void syncTargetLikeCount(String redisKeyPrefix, boolean isBlog) {
        // 模糊匹配所有该类型的点赞计数键（如like:blog:count:123）
        Set<String> countKeys = template.keys(redisKeyPrefix + "*");
        if (CollectionUtils.isEmpty(countKeys)) {
            return;
        }

        for (String key : countKeys) {
            // 从键中解析目标ID（如从"like:blog:count:123"中提取123）
            Long targetId = Long.parseLong(key.replace(redisKeyPrefix, ""));
            // 获取Redis中存储的点赞数（HASH结构中的"like"字段）
            Object redisLikeCount = template.opsForHash().get(key, "like");
            if (redisLikeCount == null) {
                continue;
            }
            int count = Integer.parseInt(redisLikeCount.toString());

            // 更新到MySQL
            if (isBlog) {
                blogMapper.updateLikeCount(targetId, count);
            } else {
                commentMapper.updateLikeCount(targetId, count);
            }

            // 可选：同步完成后删除Redis计数（如果不需要缓存则删除，否则保留）
            // redisTemplate.delete(key);
        }
    }

}
