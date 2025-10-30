package com.star.highconcurrent.mapper;

import com.star.highconcurrent.model.dto.LikeRecordDto;
import com.star.highconcurrent.model.entity.LikeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 点赞记录表（支持博客/评论） Mapper 接口
 * </p>
 *
 * @author star
 * @since 2025-10-26
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {

    @Select("select * " +
            "from like_record " +
            "where " +
            "status = 1 and " +
            "user_id = #{userId} and " +
            "target_type = #{targetType} and " +
            "target_id = #{targetId}")
    LikeRecord selectLikeByRecord(LikeRecordDto record);

    @Insert("insert into " +
            "like_record(user_id, target_type, target_id, status) " +
            "values " +
            "(#{userId},#{targetType},#{targetId},#{status})")
    void insertByDto(LikeRecordDto record);

    @Update("update like_record set status = 0 where " +
            "user_id = #{userId} and " +
            "target_type = #{targetType} and " +
            "target_id = #{targetId}")
    void logicDelete(LikeRecord record);

    @Select("select * " +
            "from like_record " +
            "where " +
            "target_id = #{targetId} " +
            "and target_type = ${targetType} " +
            "and user_id = #{userId}")
    LikeRecord selectLikeRecordExist(LikeRecordDto record);

    @Insert("insert into like_record(user_id, target_type, target_id, status) " +
            "values (#{userId},#{targetType},#{targetId},#{status})")
    void insertByEntity(LikeRecord record);

}
