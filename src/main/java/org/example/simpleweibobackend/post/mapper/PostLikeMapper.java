package org.example.simpleweibobackend.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.post.entity.PostLike;

@Mapper
public interface PostLikeMapper extends BaseMapper<PostLike> {
}
