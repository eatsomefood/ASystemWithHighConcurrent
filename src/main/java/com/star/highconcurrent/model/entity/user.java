package com.star.highconcurrent.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author star
 * @since 2025-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class user implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（登录用）
     */
    @TableField("username")
    private String username;

    /**
     * 加密后的密码（MD5算法）
     */
    @TableField("password")
    private String password;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 邮箱（用于验证/找回密码）
     */
    @TableField("email")
    private String email;

    /**
     * 状态（0-禁用，1-正常）
     */
    @TableField("status")
    private Integer status;

    /**
     * 逻辑删除字段(0-删除,1-未删除)
     */
    @TableField("delete")
    private Integer delete;

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
