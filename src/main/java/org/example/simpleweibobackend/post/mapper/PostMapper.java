package org.example.simpleweibobackend.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.post.entity.Post;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
