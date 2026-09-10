package org.example.simpleweibobackend.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Update("""
            UPDATE post
            SET view_count = view_count + 1,
                update_time = update_time
            WHERE id = #{id}
            """)
    int incrementViewCount(@Param("id") Long id);
}
