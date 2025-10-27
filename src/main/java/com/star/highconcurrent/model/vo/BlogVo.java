package com.star.highconcurrent.model.vo;

import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.BlogContent;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.model.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
public class BlogVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

    private Long authorId;

    private String authorName;

    private String authorAvatar;

    private Integer likeCount;

    private Integer viewCount;

    private Integer commentCount;

    private LocalDateTime updateAt;

    private List<Comment> comments;

    public BlogVo(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.authorId = blog.getAuthorId();
        this.viewCount = blog.getViewCount();
        this.likeCount = blog.getLikeCount();
        this.commentCount = blog.getCommentCount();
        this.updateAt = blog.getUpdatedAt();
    }

    public void updateUser(Map<String,Object> user){
        this.authorName = user.get("nickName").toString();
        this.authorAvatar = user.get("avatar").toString();
    }

    public void updateUser(UserVo user){
        this.authorName = user.getNickName();
        this.authorAvatar = user.getAvatar();
    }

    public void updateContent(BlogContent content){
        this.content = content.getContent();
    }

}
