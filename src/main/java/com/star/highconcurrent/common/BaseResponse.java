package com.star.highconcurrent.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 继承序列化接口方便传输
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code,data,"");
    }
}
