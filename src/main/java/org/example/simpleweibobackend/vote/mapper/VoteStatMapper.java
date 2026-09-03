package org.example.simpleweibobackend.vote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.simpleweibobackend.vote.entity.VoteStat;

@Mapper
public interface VoteStatMapper extends BaseMapper<VoteStat> {

    @Update("""
            UPDATE vote_stat
            SET vote_count = vote_count + #{delta}
            WHERE activity_id = #{activityId}
              AND post_id = #{postId}
            """)
    int incrementCount(@Param("activityId") Long activityId,
                       @Param("postId") Long postId,
                       @Param("delta") Long delta);
}
