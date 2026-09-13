package org.example.simpleweibobackend.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper,基础 CRUD 由 {@link BaseMapper} 提供
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
