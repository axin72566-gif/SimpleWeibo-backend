package org.example.simpleweibobackend.post.like;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostLikeMapper extends BaseMapper<PostLike> {

    /** 批量插入,INSERT IGNORE + 唯一索引保证幂等 */
    @Insert("<script>"
            + "INSERT IGNORE INTO post_like(user_id, post_id) VALUES "
            + "<foreach collection='likes' item='l' separator=','>(#{l.userId}, #{l.postId})</foreach>"
            + "</script>")
    int insertBatch(@Param("likes") List<PostLike> likes);
}
