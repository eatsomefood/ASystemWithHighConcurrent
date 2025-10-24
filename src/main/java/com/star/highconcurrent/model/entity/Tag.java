package com.star.highconcurrent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 标签表
 * </p>
 *
 * @author star
 * @since 2025-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    @TableField("name")
    private String name;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;


}
