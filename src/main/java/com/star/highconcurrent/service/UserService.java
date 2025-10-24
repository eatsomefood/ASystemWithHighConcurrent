package com.star.highconcurrent.service;

import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.model.entity.Login;
import com.star.highconcurrent.model.entity.User;

public interface UserService {

    BaseResponse<String> register(User user);

    BaseResponse<String> login(Login login);
}
