package org.example.simpleweibobackend.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Select("""
            SELECT *
            FROM post
            WHERE create_time >= #{startTime} AND create_time <= #{endTime}
            ORDER BY create_time DESC, id DESC
            LIMIT #{limit}
            """)
    List<Post> selectPreHostPosts(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("limit") int limit);
}
