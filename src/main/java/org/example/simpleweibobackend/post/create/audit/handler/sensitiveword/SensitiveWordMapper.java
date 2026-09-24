package org.example.simpleweibobackend.post.create.audit.handler.sensitiveword;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 敏感词表 Mapper
 */
@Mapper
public interface SensitiveWordMapper extends BaseMapper<SensitiveWord> {
}
