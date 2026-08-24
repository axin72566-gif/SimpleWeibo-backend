package org.example.simpleweibobackend.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.simpleweibobackend.post.entity.Inbox;

import java.util.List;

@Mapper
public interface InboxMapper extends BaseMapper<Inbox> {

    @Insert("<script>" +
            "INSERT IGNORE INTO inbox (receiver_id, post_id, author_id) VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.receiverId}, #{item.postId}, #{item.authorId})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("items") List<Inbox> items);
}
