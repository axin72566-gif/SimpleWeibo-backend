package org.example.simpleweibobackend.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.user.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
