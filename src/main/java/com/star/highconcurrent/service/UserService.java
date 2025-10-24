package com.star.highconcurrent.service;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.MD5;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.mapper.UserMapper;
import com.star.highconcurrent.model.entity.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Resource
    private UserMapper mapper;

    public BaseResponse<String> register(User user) {
        // 尝试密码MD5加密
        String originPW = user.getPassword();
        String hashpw = BCrypt.hashpw(originPW);
        user.setPassword(hashpw);
        // 加密后存入数据库
        try {
            int change = mapper.insert(user);
            if (change > 0){
                return new BaseResponse<>(Code.OK);
            }else {
                return new BaseResponse<>(Code.REGISTER_ERROR);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
