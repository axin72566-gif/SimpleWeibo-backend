package org.example.simpleweibobackend.post.feed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.simpleweibobackend.post.feed.entity.FeedItem;

import java.util.List;

@Mapper
public interface FeedItemMapper extends BaseMapper<FeedItem> {

    @Insert("<script>" +
            "INSERT IGNORE INTO feed_item (user_id, post_id, post_user_id) VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.userId}, #{item.postId}, #{item.postUserId})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("items") List<FeedItem> items);
}
