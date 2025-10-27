package com.star.highconcurrent.model.vo;

import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.BlogContent;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.model.entity.User;
import com.star.highconcurrent.util.BeanUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    public Map<String, Object> getBlogMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("content", content);
        map.put("authorId", authorId);
        map.put("authorName", authorName);
        map.put("authorAvatar", authorAvatar);
        map.put("likeCount", likeCount);
        map.put("viewCount", viewCount);
        map.put("commentCount", commentCount);
        map.put("updateAt", updateAt);
        map.put("comments", comments);
        return map;
    }

    public static BlogVo getBlog(Map<Object, Object> map) {
        BlogVo blogVo = new BlogVo();
        BeanUtil.copyBean(blogVo,map);
        return blogVo;
    }

    public BlogVo(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.authorId = blog.getAuthorId();
        this.viewCount = blog.getViewCount();
        this.likeCount = blog.getLikeCount();
        this.commentCount = blog.getCommentCount();
        this.updateAt = blog.getUpdatedAt();
    }

    public void updateUser(Map<String, Object> user) {
        this.authorName = user.get("nickName").toString();
        this.authorAvatar = user.get("avatar").toString();
    }

    public void updateUser(UserVo user) {
        this.authorName = user.getNickName();
        this.authorAvatar = user.getAvatar();
    }

    public void updateContent(BlogContent content) {
        this.content = content.getContent();
    }

}
