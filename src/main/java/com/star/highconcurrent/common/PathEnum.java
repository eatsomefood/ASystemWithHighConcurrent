package com.star.highconcurrent.common;

import lombok.Getter;

@Getter
public enum PathEnum {
    USER_LOGIN("user:login:"),
    BLOG_LIKE("blog:like:"),
    COMMENT_LIKE("comment:like:"),
    LOCK_VALUE("lock:"),
    HOT_BLOG("hot:blog"),
    GET_BLOG("blog:content:"),
    BLOG_LIKE_COUNT("blog:like:count:"),
    COMMENT_LIKE_COUNT("comment:like:count:"),
    USER_LIKE("user:like:");

    private final String path;

    PathEnum(String path) {
        this.path = path;
    }

}
