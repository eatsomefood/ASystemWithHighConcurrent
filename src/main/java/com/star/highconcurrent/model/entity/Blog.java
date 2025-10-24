package com.star.highconcurrent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 博客表（元信息）
 * </p>
 *
 * @author star
 * @since 2025-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog")
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 博客唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 博客标题
     */
    @TableField("title")
    private String title;

    /**
     * 摘要（列表页展示）
     */
    @TableField("summary")
    private String summary;

    /**
     * 作者ID（关联user表）
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 封面图URL
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 状态（0-草稿，1-发布，2-删除）
     */
    @TableField("status")
    private Integer status;

    /**
     * 阅读量（冗余字段）
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 点赞数（冗余字段）
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论数（冗余字段）
     */
    @TableField("comment_count")
    private Integer commentCount;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;


}
