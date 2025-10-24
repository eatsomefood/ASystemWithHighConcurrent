use highcurrent;
-- 创建存储过程：生成50个用户
DELIMITER $$
CREATE PROCEDURE generate_users()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 50 DO
            INSERT INTO `user` (username, password, nickname, email, status)
            VALUES (
                       CONCAT('user_', i),  -- 用户名：user_1到user_50（唯一）
                       '$2a$10$xxxx',       -- 加密密码占位符
                       CONCAT('昵称_', i),  -- 昵称
                       CONCAT('user_', i, '@example.com'),  -- 邮箱：唯一
                       FLOOR(RAND() * 2)    -- 状态：0或1（随机）
                   );
            SET i = i + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程
CALL generate_users();

-- 验证：查询用户数量
SELECT COUNT(*) FROM `user`; -- 应返回50
-- 创建存储过程：生成50篇博客（关联用户表）
DELIMITER $$
CREATE PROCEDURE generate_blogs()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 50 DO
            INSERT INTO `blog` (title, summary, author_id, cover_image, status, view_count, like_count, comment_count)
            VALUES (
                       CONCAT('博客标题_', i),  -- 标题
                       CONCAT('这是第', i, '篇博客的摘要...'),  -- 摘要
                       FLOOR(RAND() * 50) + 1, -- 作者ID：1-50（关联user表）
                       CONCAT('https://img.example.com/', i, '.jpg'),  -- 封面图
                       FLOOR(RAND() * 3),      -- 状态：0（草稿）、1（发布）、2（删除）
                       FLOOR(RAND() * 1000),   -- 阅读量：0-999
                       FLOOR(RAND() * 200),    -- 点赞数：0-199
                       FLOOR(RAND() * 50)      -- 评论数：0-49
                   );
            SET i = i + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程
CALL generate_blogs();

-- 验证
SELECT COUNT(*) FROM `blog`; -- 应返回50

-- 创建存储过程：为每个博客生成正文（一对一关联）
DELIMITER $$
CREATE PROCEDURE generate_blog_contents()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 50 DO
            INSERT INTO `blog_content` (blog_id, content)
            VALUES (
                       i,  -- 关联博客ID：1-50（与blog表一一对应）
                       CONCAT('### 博客正文 ', i, '\n\n这是第', i, '篇博客的详细内容，支持Markdown格式。\n\n段落1：测试内容...\n\n段落2：更多测试内容...')
                   );
            SET i = i + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程
CALL generate_blog_contents();

-- 验证
SELECT COUNT(*) FROM `blog_content`; -- 应返回50

-- 创建存储过程：生成100条点赞记录（避免重复）
DELIMITER $$
CREATE PROCEDURE generate_like_records()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_id INT;
    DECLARE target_type INT;
    DECLARE target_id INT;
    -- 先确保有足够的评论（后续会生成），这里先为博客点赞
    WHILE i <= 100 DO
            SET user_id = FLOOR(RAND() * 50) + 1; -- 用户1-50
            SET target_type = FLOOR(RAND() * 2) + 1; -- 1=博客，2=评论
            -- 目标ID：博客1-50，评论后续生成（先假设评论1-50）
            SET target_id = IF(target_type=1, FLOOR(RAND() * 50) + 1, FLOOR(RAND() * 50) + 1);
            -- 尝试插入，若重复则跳过（利用唯一键约束）
            BEGIN
                DECLARE CONTINUE HANDLER FOR 1062 BEGIN END; -- 捕获重复键错误
                INSERT INTO `like_record` (user_id, target_type, target_id, status)
                VALUES (user_id, target_type, target_id, 1); -- 1=点赞状态
            END;
            SET i = i + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程
CALL generate_like_records();

-- 验证（可能少于100，因去重）
SELECT COUNT(*) FROM `like_record`; -- 至少50条

-- 创建存储过程：生成100条评论（支持一级和二级评论）
DELIMITER $$
CREATE PROCEDURE generate_comments()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE blog_id INT;
    DECLARE user_id INT;
    DECLARE parent_id INT;
    WHILE i <= 100 DO
            SET blog_id = FLOOR(RAND() * 50) + 1; -- 关联博客1-50
            SET user_id = FLOOR(RAND() * 50) + 1; -- 评论用户1-50
            -- 30%概率为二级评论（parent_id为已存在的评论ID）
            SET parent_id = IF(RAND() < 0.3, FLOOR(RAND() * (i-1)) + 1, 0);
            INSERT INTO `comment` (blog_id, user_id, parent_id, content, status, like_count)
            VALUES (
                       blog_id,
                       user_id,
                       parent_id,
                       CONCAT('评论内容_', i, '：这是一条测试评论...'), -- 内容
                       1, -- 状态：正常（1）
                       FLOOR(RAND() * 50) -- 点赞数0-49
                   );
            SET i = i + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程（需在blog表之后执行）
CALL generate_comments();

-- 验证
SELECT COUNT(*) FROM `comment`; -- 应返回100

-- 创建存储过程：生成50个唯一标签
DELIMITER $$
CREATE PROCEDURE generate_tags()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 50 DO
            INSERT INTO `tag` (name)
            VALUES (CONCAT('标签_', i)); -- 标签名唯一：标签_1到标签_50
            SET i = i + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程
CALL generate_tags();

-- 验证
SELECT COUNT(*) FROM `tag`; -- 应返回50

-- 创建存储过程：为每个博客关联3个标签（避免重复）
DELIMITER $$
CREATE PROCEDURE generate_blog_tags()
BEGIN
    DECLARE blog_id INT DEFAULT 1;
    DECLARE tag_idx INT;
    DECLARE tag_id INT;
    -- 遍历每个博客（1-50）
    WHILE blog_id <= 50 DO
            SET tag_idx = 1;
            -- 每个博客关联3个不同标签
            WHILE tag_idx <= 3 DO
                    SET tag_id = FLOOR(RAND() * 50) + 1; -- 随机标签1-50
                    -- 避免重复关联
                    BEGIN
                        DECLARE CONTINUE HANDLER FOR 1062 BEGIN END;
                        INSERT INTO `blog_tag` (blog_id, tag_id)
                        VALUES (blog_id, tag_id);
                    END;
                    SET tag_idx = tag_idx + 1;
                END WHILE;
            SET blog_id = blog_id + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程
CALL generate_blog_tags();

-- 验证
SELECT COUNT(*) FROM `blog_tag`; -- 约150条（可能略少，因去重）

-- 创建存储过程：生成用户关注关系（避免重复和自关注）
DELIMITER $$
CREATE PROCEDURE generate_user_follows()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_id INT;
    DECLARE follow_user_id INT;
    WHILE i <= 200 DO
            SET user_id = FLOOR(RAND() * 50) + 1;
            -- 确保不关注自己
            REPEAT
                SET follow_user_id = FLOOR(RAND() * 50) + 1;
            UNTIL follow_user_id != user_id END REPEAT;
            -- 避免重复关注
            BEGIN
                DECLARE CONTINUE HANDLER FOR 1062 BEGIN END;
                INSERT INTO `user_follow` (user_id, follow_user_id, status)
                VALUES (user_id, follow_user_id, 1); -- 1=已关注
            END;
            SET i = i + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程
CALL generate_user_follows();

-- 验证
SELECT COUNT(*) FROM `user_follow`; -- 至少50条

-- 创建存储过程：生成通知（关联用户、博客/评论）
DELIMITER $$
CREATE PROCEDURE generate_notifications()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE user_id INT;
    DECLARE type INT;
    DECLARE related_id INT;
    WHILE i <= 150 DO
            SET user_id = FLOOR(RAND() * 50) + 1; -- 接收通知用户1-50
            SET type = FLOOR(RAND() * 3) + 1; -- 1=点赞，2=评论，3=关注
            -- 关联ID：点赞/评论关联博客1-50或评论1-100，关注关联用户1-50
            SET related_id = IF(type=3, FLOOR(RAND() * 50) + 1,
                                IF(RAND() < 0.5, FLOOR(RAND() * 50) + 1, FLOOR(RAND() * 100) + 1));
            INSERT INTO `notification` (user_id, type, content, related_id, is_read)
            VALUES (
                       user_id,
                       type,
                       CASE type
                           WHEN 1 THEN CONCAT('用户', FLOOR(RAND() * 50) + 1, '点赞了你的内容')
                           WHEN 2 THEN CONCAT('用户', FLOOR(RAND() * 50) + 1, '评论了你的博客')
                           WHEN 3 THEN CONCAT('用户', FLOOR(RAND() * 50) + 1, '关注了你')
                           END,
                       related_id,
                       FLOOR(RAND() * 2) -- 0=未读，1=已读
                   );
            SET i = i + 1;
        END WHILE;
END $$
DELIMITER ;

-- 执行存储过程
CALL generate_notifications();

-- 验证
SELECT COUNT(*) FROM `notification`; -- 应返回150