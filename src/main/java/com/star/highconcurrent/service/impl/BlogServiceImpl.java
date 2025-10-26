package com.star.highconcurrent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.highconcurrent.common.BaseResponse;
import com.star.highconcurrent.common.Code;
import com.star.highconcurrent.mapper.BlogContentMapper;
import com.star.highconcurrent.mapper.BlogMapper;
import com.star.highconcurrent.mapper.CommentMapper;
import com.star.highconcurrent.mapper.UserMapper;
import com.star.highconcurrent.model.entity.Blog;
import com.star.highconcurrent.model.entity.BlogContent;
import com.star.highconcurrent.model.entity.Comment;
import com.star.highconcurrent.model.entity.User;
import com.star.highconcurrent.model.vo.BlogVo;
import com.star.highconcurrent.service.BlogService;
import com.star.highconcurrent.util.UserContext;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.plaf.basic.BasicSeparatorUI;
import java.sql.Wrapper;
import java.util.List;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Value("${com.star.comment.page-size:10}")
    private int pageSize;

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private BlogContentMapper blogContentMapper;

    @Resource
    private CommentMapper commentMapper;

    @Override
    public BaseResponse getDeclareBlogById(long id) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            return new BaseResponse<>(Code.DATABASE_ERROR);
        } else {
            BlogVo vo = new BlogVo(blog);
            vo.updateUser(UserContext.getUser());
            BlogContent blogContent = blogContentMapper.selectContentByAuthorId(blog.getAuthorId());
            if (blogContent == null) {
                log.error("当前文章内容不存在，请查看: " + blog.getId().toString());
                return new BaseResponse(Code.DATABASE_ERROR);
            }
            vo.updateContent(blogContent);
            Page<Comment> page = new Page<>(0, pageSize);
            QueryWrapper<Comment> queryWrapper = Wrappers.<Comment>query().
                    eq("blog_id", blog.getId()).
                    orderByDesc("created_at");
            Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
            List<Comment> records = commentPage.getRecords();
            vo.setComments(records);
            return new BaseResponse<>(Code.OK, vo);
        }
    }

    @Override
    public BaseResponse<List<Blog>> getListByPage(com.star.highconcurrent.model.entity.Page page) {
        List<Blog> records = null;
        try {
            Page<Blog> wrapperPage = new Page<Blog>();
            if (page == null) {
                wrapperPage.setCurrent(1);
                wrapperPage.setSize(pageSize);
            } else {
                int size = page.getPageSize();
                int pageNum = page.getPageNum();
                if (size <= 0) {
                    wrapperPage.setSize(pageSize);
                } else {
                    wrapperPage.setCurrent(size);
                }
                if (pageNum <= 0) {
                    wrapperPage.setCurrent(1);
                } else {
                    wrapperPage.setCurrent(pageNum);
                }
            }
            // 构建page对象后，开始构建queryWrapper对象
            QueryWrapper<Blog> listQueryWrapper = Wrappers.<Blog>query()
                    .eq("status", 2)
                    .orderByDesc("created_at");
            Page<Blog> selectPage = blogMapper.selectPage(wrapperPage, listQueryWrapper);
            records = selectPage.getRecords();
        } catch (Exception e) {
            log.error(e.getMessage());
            return new BaseResponse<>(Code.ERROR);
        }
        return new BaseResponse<>(Code.OK, records);
    }
}
