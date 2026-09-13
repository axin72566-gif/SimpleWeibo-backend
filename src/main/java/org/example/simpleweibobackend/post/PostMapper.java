package org.example.simpleweibobackend.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子表 Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 查询指定发布时间段内的帖子,按发布时间倒序,供热榜召回层使用
     *
     * @param startTime 起始时间(含)
     * @param endTime   截止时间(含)
     * @param limit     最多返回条数
     */
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
