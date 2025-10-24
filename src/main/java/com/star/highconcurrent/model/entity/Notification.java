package com.star.highconcurrent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 通知表
 * </p>
 *
 * @author star
 * @since 2025-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接收通知的用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 类型（1-点赞，2-评论，3-关注）
     */
    @TableField("type")
    private Integer type;

    /**
     * 通知内容
     */
    @TableField("content")
    private String content;

    /**
     * 关联ID（如博客ID/评论ID）
     */
    @TableField("related_id")
    private Long relatedId;

    /**
     *  是否已读（0-未读，1-已读）
     */
    @TableField("is_read")
    private Integer isRead;

    /**
     * 通知时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;


}
