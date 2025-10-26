package com.star.highconcurrent.common;

public enum PathEnum {
    USER_LOGIN("user:login:"),
    USER_LIKE("blog:like:");

    private final String path;

    PathEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
