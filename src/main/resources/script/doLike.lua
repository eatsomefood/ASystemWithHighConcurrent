-- 视频键
local likeSetKey = KEYS[1]
-- 视频点赞计数键
local blogOrCommentLikeCountKey = KEYS[2]
-- 用户点赞记录键
local userLikeBlogOrCommentKey = KEYS[3]
-- 点赞用户ID
local userId = ARGV[1]
-- 点赞博客或者评论ID
local blogOrCommentId = ARGV[2]
-- 目标操作状态（0:取消点赞, 1:点赞）
local targetStatus = ARGV[3]
-- 目标视频或者博客的初始点赞数
local likeCount = ARGV[4]
-- 当前时间戳
local timeArr = redis.call('TIME')
local seconds = tonumber(timeArr[1])  -- 秒（转换为数字）
local microseconds = tonumber(timeArr[2])  -- 微秒（转换为数字）
local millisecondTimestamp = seconds * 1000 + math.floor(microseconds / 1000)  -- 毫秒级时间戳

-- 校验状态参数合法性
if targetStatus ~= "0" and targetStatus ~= "1" then
    return 0  -- 非法状态返回0
end

local status = redis.call("HEXISTS", blogOrCommentLikeCountKey, "like")

if status == 0 then
    redis.call("HSET", blogOrCommentLikeCountKey, "like", likeCount)
end

-- 处理取消点赞操作
if targetStatus == "0" then
    -- 直接移除用户，SREM对不存在的元素操作是安全的，无需提前判断
    redis.call("SREM", likeSetKey, userId)
    -- 点赞数量减一
    redis.call("HINCRBY", blogOrCommentLikeCountKey, "like", -1)
    -- 用户点赞移除
    redis.call("ZREM", userLikeBlogOrCommentKey, blogOrCommentId)
    return 1
end

-- 处理点赞操作（targetStatus == "1"）
-- 直接添加用户，SADD会自动忽略已存在的元素，无需提前判断
redis.call("SADD", likeSetKey, userId)
-- 点赞数量减一
redis.call("HINCRBY", blogOrCommentLikeCountKey, "like", 1)
-- 用户点赞移除
redis.call("ZADD", userLikeBlogOrCommentKey, "NX", millisecondTimestamp, blogOrCommentId)
return -1