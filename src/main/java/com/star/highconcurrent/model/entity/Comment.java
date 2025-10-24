package com.star.highconcurrent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 评论表（支持多级回复）
 * </p>
 *
 * @author star
 * @since 2025-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联博客ID
     */
    @TableField("blog_id")
    private Long blogId;

    /**
     * 评论用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 父评论ID（0表示一级评论）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 状态（0-删除，1-正常）
     */
    @TableField("status")
    private Integer status;

    /**
     * 评论点赞数（冗余）
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;


}
