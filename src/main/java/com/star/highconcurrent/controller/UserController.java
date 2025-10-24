package com.star.highconcurrent.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.model.entity.User;
import com.star.highconcurrent.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RequestMapping("/user")
@RestController
@Slf4j
/**
 * 用户模块，用来查询用户的登录注册等
 */
public class UserController {

    @Resource
    private UserService service;

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Operation(description = "用户注册")
    @PostMapping("/register")
    public BaseResponse<String> register(@RequestBody User user){
        // 健壮性校验
        if (user == null){
            return new BaseResponse<>(Code.REGISTER_ERROR);
        }
        String username = user.getUsername();
        String password = user.getPassword();
        String nickname = user.getNickname();
        String email = user.getEmail();
        // 非空性校验
        if (StringUtils.isAnyBlank(username,password,email)){
            return new BaseResponse<>(Code.REGISTER_ERROR.getCode(),"","信息为空，请填写后重新注册");
        }
        // 开始注册
        return service.register(user);
    }



}
