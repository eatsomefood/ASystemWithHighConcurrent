package com.star.highconcurrent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.star.highconcurrent.model.entity.User;
import com.star.highconcurrent.model.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where email = #{email} and is_delete = 1 and status = 1")
    User selectByEmail(String email);

    @Select("select id,user.username,user.nickname,user.avatar from user where id = #{authorId}")
    UserVo selectVoById(Long authorId);
}
