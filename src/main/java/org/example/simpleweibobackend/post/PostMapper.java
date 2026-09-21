package org.example.simpleweibobackend.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子表 Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
}
