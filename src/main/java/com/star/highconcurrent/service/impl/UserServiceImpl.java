package com.star.highconcurrent.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.mapper.UserMapper;
import com.star.highconcurrent.model.entity.Login;
import com.star.highconcurrent.model.entity.User;
import com.star.highconcurrent.service.UserService;
import com.star.highconcurrent.util.JWTUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
    @Resource
    private UserMapper mapper;

    @Resource
    private RedissonClient client;

    @Resource
    private JWTUtil jwtUtil;

    private final String USER_KEY = "user:";

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public BaseResponse<String> register(User user) {
        // 尝试密码MD5加密
        String originPW = user.getPassword();
        String hashpw = BCrypt.hashpw(originPW);
        user.setPassword(hashpw);
        // 加密后存入数据库
        try {
            int change = mapper.insert(user);
            if (change > 0) {
                return new BaseResponse<>(Code.OK);
            } else {
                return new BaseResponse<>(Code.REGISTER_ERROR);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BaseResponse<String> login(Login login) {
        String email = login.getEmail();
        String password = login.getPassword();
        // 获取具体用户信息
        User user = mapper.selectByEmail(email);
        if (user == null) {
            return new BaseResponse<>(Code.USER_NOT_FOUND);
        }
        // 对密码进行校验
        if (BCrypt.checkpw(password, user.getPassword())) {
            // 校验成功，用户登录
            // 登录成功，把当前用户以Jwt令牌的形式存入redis中
            // 签发JWT令牌
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",user.getId());
            claims.put("nickName",user.getNickname());
            claims.put("email",user.getEmail());
            claims.put("avatar",user.getAvatar());
            String jwt = jwtUtil.createJwt(claims);
            redisTemplate.opsForValue().set(USER_KEY + "login:" + user.getId(),jwt);
            return new BaseResponse<>(Code.LOGIN_SUCCESS, jwt);
        } else {
            return new BaseResponse<>(Code.PASSWORD_ERROR);
        }
    }
}
