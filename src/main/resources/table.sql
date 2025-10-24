-- 创建数据库
create database IF NOT EXISTS HighCurrent;
use HighCurrent;
-- 创建数据库表
-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID',
                                      `username` varchar(50) NOT NULL COMMENT '用户名（登录用）',
                                      `password` varchar(128) NOT NULL COMMENT '加密后的密码（MD5算法）',
                                      `nickname` varchar(50) DEFAULT '' COMMENT '用户昵称',
                                      `avatar` varchar(255) DEFAULT '' COMMENT '头像URL',
                                      `email` varchar(100) NOT NULL COMMENT '邮箱（用于验证/找回密码）',
                                      `status` tinyint DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
                                      `is_delete` tinyint DEFAULT 1 COMMENT '逻辑删除字段(0-删除,1-未删除)',
                                      `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uk_username` (`username`),
                                      UNIQUE KEY `uk_email` (`email`),
                                      INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 博客表
CREATE TABLE IF NOT EXISTS `blog` (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT '博客唯一ID',
                                      `title` varchar(200) NOT NULL COMMENT '博客标题',
                                      `summary` varchar(500) DEFAULT '' COMMENT '摘要（列表页展示）',
                                      `author_id` bigint NOT NULL COMMENT '作者ID（关联user表）',
                                      `cover_image` varchar(255) DEFAULT '' COMMENT '封面图URL',
                                      `status` tinyint DEFAULT 0 COMMENT '状态（0-草稿，1-发布，2-删除）',
                                      `view_count` int DEFAULT 0 COMMENT '阅读量（冗余字段）',
                                      `like_count` int DEFAULT 0 COMMENT '点赞数（冗余字段）',
                                      `comment_count` int DEFAULT 0 COMMENT '评论数（冗余字段）',
                                      `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      INDEX `idx_author_id` (`author_id`),
                                      INDEX `idx_status` (`status`),
                                      INDEX `idx_like_count` (`like_count`),
                                      INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客表（元信息）';

-- 博客内容表(后续同步到es中)
CREATE TABLE IF NOT EXISTS `blog_content` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                              `blog_id` bigint NOT NULL COMMENT '关联博客ID（一对一）',
                                              `content` longtext NOT NULL COMMENT '博客正文（支持Markdown/HTML）',
                                              `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `is_delete` tinyint default 1 COMMENT '0-删除,1-正常',
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `uk_blog_id` (`blog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客内容表（大文本拆分）';

-- 点赞内容表
CREATE TABLE IF NOT EXISTS `like_record` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                             `user_id` bigint NOT NULL COMMENT '点赞用户ID',
                                             `target_type` tinyint NOT NULL COMMENT '目标类型（1-博客，2-评论）',
                                             `target_id` bigint NOT NULL COMMENT '目标ID（博客ID/评论ID）',
                                             `status` tinyint DEFAULT 1 COMMENT '状态（1-点赞，0-取消点赞）',
                                             `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                             PRIMARY KEY (`id`),
                                             UNIQUE KEY `uk_user_target` (`user_id`,`target_type`,`target_id`), -- 防止重复点赞
                                             INDEX `idx_target_status` (`target_type`,`target_id`,`status`), -- 快速统计有效点赞
                                             CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞记录表（支持博客/评论）';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论唯一ID',
                                         `blog_id` bigint NOT NULL COMMENT '关联博客ID',
                                         `user_id` bigint NOT NULL COMMENT '评论用户ID',
                                         `parent_id` bigint DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
                                         `content` varchar(500) NOT NULL COMMENT '评论内容',
                                         `status` tinyint DEFAULT 1 COMMENT '状态（0-删除，1-正常）',
                                         `like_count` int DEFAULT 0 COMMENT '评论点赞数（冗余）',
                                         `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         PRIMARY KEY (`id`),
                                         INDEX `idx_blog_id` (`blog_id`),
                                         INDEX `idx_user_id` (`user_id`),
                                         INDEX `idx_parent_id` (`parent_id`),
                                         INDEX `idx_status` (`status`),
                                         INDEX `idx_created_at` (`created_at`),
                                         CONSTRAINT `fk_comment_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`) ON DELETE CASCADE,
                                         CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表（支持多级回复）';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签唯一ID',
                                     `name` varchar(30) NOT NULL COMMENT '标签名称',
                                     `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (`id`),
                                     UNIQUE KEY `uk_tag_name` (`name`) -- 标签名称唯一
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 博客-标签关联表
CREATE TABLE IF NOT EXISTS `blog_tag` (
                                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                          `blog_id` bigint NOT NULL COMMENT '博客ID',
                                          `tag_id` bigint NOT NULL COMMENT '标签ID',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `uk_blog_tag` (`blog_id`,`tag_id`), -- 防止重复关联
                                          INDEX `idx_tag_id` (`tag_id`),
                                          CONSTRAINT `fk_blogtag_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`) ON DELETE CASCADE,
                                          CONSTRAINT `fk_blogtag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客-标签关联表（多对多）';

-- 用户关注表
CREATE TABLE IF NOT EXISTS `user_follow` (
                                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
                                             `user_id` bigint NOT NULL COMMENT '关注者ID（主动关注方）',
                                             `follow_user_id` bigint NOT NULL COMMENT '被关注者ID',
                                             `status` tinyint DEFAULT 1 COMMENT '状态（1-已关注，0-已取消）',
                                             `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
                                             PRIMARY KEY (`id`),
                                             UNIQUE KEY `uk_user_follow` (`user_id`,`follow_user_id`), -- 防止重复关注
                                             INDEX `idx_follow_user` (`follow_user_id`), -- 查粉丝列表用
                                             CONSTRAINT `fk_follow_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                                             CONSTRAINT `fk_followed_user` FOREIGN KEY (`follow_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

-- 通知表，后续替换成mq
CREATE TABLE IF NOT EXISTS `notification` (
                                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
                                              `user_id` bigint NOT NULL COMMENT '接收通知的用户ID',
                                              `type` tinyint NOT NULL COMMENT '类型（1-点赞，2-评论，3-关注）',
                                              `content` varchar(200) NOT NULL COMMENT '通知内容',
                                              `related_id` bigint DEFAULT 0 COMMENT '关联ID（如博客ID/评论ID）',
                                              `is_read` tinyint DEFAULT 0 COMMENT ' 是否已读（0-未读，1-已读）',
                                              `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',
                                              PRIMARY KEY (`id`),
                                              INDEX `idx_user_read` (`user_id`,`is_read`), -- 快速查未读通知
                                              INDEX `idx_created_at` (`created_at`),
                                              CONSTRAINT `fk_notify_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';