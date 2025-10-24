package com.star.highconcurrent.util;

import java.util.Map;

/**
 * ThreadLocal处理
 */
public class UserContext {

    private static ThreadLocal<Map<String,Object>> threadLocal;

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
