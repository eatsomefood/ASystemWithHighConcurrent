package com.star.highconcurrent.interceptor;

import com.star.highconcurrent.util.JWTUtil;
import com.star.highconcurrent.util.UserContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${com.star.jwt.token-name:auth}")
    private static String tokenName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是controller方法，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 校验jwt令牌
        String header = request.getHeader(tokenName);
        try {
            // 解析
            log.info("正在解析jwt令牌:{}",header);
            Map<String, Object> map = JWTUtil.parseJWT(header);
            UserContext.putUser(map);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
