package com.star.highconcurrent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 博客-标签关联表（多对多）
 * </p>
 *
 * @author star
 * @since 2025-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("blog_tag")
public class BlogTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 博客ID
     */
    @TableField("blog_id")
    private Long blogId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;


}
