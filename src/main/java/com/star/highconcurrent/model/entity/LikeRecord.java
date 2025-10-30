package com.star.highconcurrent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.star.highconcurrent.model.dto.LikeRecordDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 点赞记录表（支持博客/评论）
 * </p>
 *
 * @author star
 * @since 2025-10-24
 */
@ToString
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("like_record")
public class LikeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 点赞用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 目标类型（1-博客，2-评论）
     */
    @TableField("target_type")
    private Integer targetType;

    /**
     * 目标ID（博客ID/评论ID）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 状态（1-点赞，0-取消点赞）
     */
    @TableField("status")
    private Integer status;

    /**
     * 操作时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    public static LikeRecord transferDtoToEntity(LikeRecordDto recordDto){
        LikeRecord likeRecord = new LikeRecord();
        likeRecord.setUserId(recordDto.getUserId());
        likeRecord.setTargetType(recordDto.getTargetType());
        likeRecord.setTargetId(recordDto.getTargetId());
        likeRecord.setStatus(recordDto.getStatus());
        return likeRecord;
    }

}
