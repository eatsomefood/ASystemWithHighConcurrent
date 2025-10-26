package com.star.highconcurrent.common;

public enum Code {

    OK(200,"一切正常"),
    LOGIN_SUCCESS(201,"登录成功"),
    LIKE_SUCCESS(202,"点赞成功"),
    REGISTER_ERROR(401,"注册信息异常，检查后重试"),
    LOGIN_ERROR(402,"登录信息错误，请重试"),
    USER_NOT_FOUND(403,"当前用户不存在，请尝试重新输入"),
    PASSWORD_ERROR(405,"用户密码错误，请重试"),
    PARAM_ERROR(406,"参数错误，请重试"),
    ERROR(500,"后端错误，请稍后再试"),
    DATABASE_ERROR(501,"数据库数据错误，请稍后再试"),
    LIKE_FAIL(502,"点赞失败");

    private final int code;

    private final String message;

    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
