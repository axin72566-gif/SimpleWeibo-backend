package org.example.simpleweibobackend.follow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.follow.entity.Follow;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {
}
