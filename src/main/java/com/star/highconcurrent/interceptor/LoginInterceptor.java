package com.star.highconcurrent.interceptor;

import com.star.highconcurrent.common.PathEnum;
import com.star.highconcurrent.util.JWTUtil;
import com.star.highconcurrent.util.UserContext;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${com.star.jwt.token-name:auth}")
    private String tokenName;

    @Resource
    private RedisTemplate<String,Object> template;

    @Resource
    private JWTUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("拦截到的请求路径：{}", request.getRequestURI());
        // 如果不是controller方法，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 校验jwt令牌
        String header = request.getHeader(tokenName);
        if (header == null){
            // 前后端分离：返回 401 状态码，不直接重定向
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 未授权
            // 可选：返回 JSON 提示信息
            response.setContentType("application/json;charset=UTF-8");
            ServletOutputStream os = response.getOutputStream();
            os.write("{\"code\":401,\"msg\":\"未登录或token失效\"}".getBytes());
            os.flush();
            os.close();
            return false;
        }
        try {
            // 解析
            log.info("正在解析jwt令牌:{}",header);
            Map<String, Object> map = jwtUtil.parseJWT(header);
            // 有jwt看看是否是最新的
            Object id = map.get("id");
            String key = PathEnum.USER_LOGIN.getPath() + id;
            String redisJwt = template.opsForValue().get(key).toString();
            if (!redisJwt.equals(header)){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 未授权
                // 可选：返回 JSON 提示信息
                response.setContentType("application/json;charset=UTF-8");
                ServletOutputStream os = response.getOutputStream();
                os.write("{\"code\":402,\"msg\":\"当前令牌已失效\"}".getBytes());
                os.flush();
                os.close();
            }
            UserContext.putUser(map);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            // 前后端分离：返回 401 状态码，不直接重定向
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 未授权
            // 可选：返回 JSON 提示信息
            response.setContentType("application/json;charset=UTF-8");
            ServletOutputStream os = response.getOutputStream();
            os.write("{\"code\":401,\"msg\":\"未登录或token失效\"}".getBytes());
            os.flush();
            os.close();
            return false;
        }

    }
}
