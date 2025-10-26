package com.star.highconcurrent.util;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ThreadLocal处理
 */
@Component
public class UserContext {

    private static ThreadLocal<Map<String,Object>> threadLocal = new ThreadLocal<>() ;

    public static Map<String,Object> getUser(){
        return threadLocal.get();
    }

    public static void putUser(Map<String,Object> map){
        threadLocal.set(map);
    }

    public static void removeUser(){
        threadLocal.remove();
    }
}
