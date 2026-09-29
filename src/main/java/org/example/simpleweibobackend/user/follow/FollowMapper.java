package org.example.simpleweibobackend.user.follow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    /** 插入 */
    @Insert("INSERT IGNORE INTO user_follow(follower_id, followed_id) VALUES(#{followerId}, #{followedId})")
    int insertIgnore(@Param("followerId") Long followerId, @Param("followedId") Long followedId);
}
