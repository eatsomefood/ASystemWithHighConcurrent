package com.star.highconcurrent.common;

public enum Code {

    OK(200,"一切正常"),
    REGISTER_ERROR(401,"注册信息异常，检查后重试");

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
