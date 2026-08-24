package org.example.simpleweibobackend.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.simpleweibobackend.user.entity.Follow;

import java.util.List;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    @Select("SELECT fan_id FROM follow WHERE following_id = #{userId}")
    List<Long> selectFanIds(@Param("userId") Long userId);
}
