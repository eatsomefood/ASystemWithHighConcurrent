-- 分布式锁ID（点赞集合的键）
local likeSetKey = KEYS[1]
-- 点赞用户ID
local userId = ARGV[1]
-- 目标操作状态（0:取消点赞, 1:点赞）
local targetStatus = ARGV[2]

-- 校验状态参数合法性
if targetStatus ~= "0" and targetStatus ~= "1" then
    return 0  -- 非法状态返回0
end

-- 处理取消点赞操作
if targetStatus == "0" then
    -- 直接移除用户，SREM对不存在的元素操作是安全的，无需提前判断
    redis.call("SREM", likeSetKey, userId)
    return 1
end

-- 处理点赞操作（targetStatus == "1"）
-- 直接添加用户，SADD会自动忽略已存在的元素，无需提前判断
redis.call("SADD", likeSetKey, userId)
return -1