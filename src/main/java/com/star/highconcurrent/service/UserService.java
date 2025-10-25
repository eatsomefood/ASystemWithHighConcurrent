package com.star.highconcurrent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.model.entity.Login;
import com.star.highconcurrent.model.entity.User;

public interface UserService extends IService<User> {

    BaseResponse<String> register(User user);

    BaseResponse<String> login(Login login);
}
