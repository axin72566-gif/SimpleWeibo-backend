package org.example.simpleweibobackend.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Select("""
            SELECT id, user_id, title, content, view_count, create_time, update_time
            FROM post
            WHERE create_time >= #{startTime} AND create_time <= #{endTime}
            """)
    List<Post> selectPostsCreatedBetween(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    @Update("""
            UPDATE post
            SET view_count = view_count + 1,
                update_time = update_time
            WHERE id = #{id}
            """)
    int incrementViewCount(@Param("id") Long id);
}
