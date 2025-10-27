package com.star.highconcurrent.common;

public enum PathEnum {
    USER_LOGIN("user:login:"),
    BLOG_LIKE("blog:like:"),
    COMMENT_LIKE("comment:like:"),
    LOCK_VALUE("lock:"),
    HOT_BLOG("hot:blog");

    private final String path;

    PathEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
