package org.example.simpleweibobackend.user.follow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.simpleweibobackend.user.follow.entity.Follow;

import java.util.List;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    @Select("SELECT follower_id FROM follow WHERE following_id = #{userId}")
    List<Long> selectFollowerIds(@Param("userId") Long userId);
}
