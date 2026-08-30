package org.example.simpleweibobackend.vote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.vote.entity.VoteRecord;

@Mapper
public interface VoteRecordMapper extends BaseMapper<VoteRecord> {
}
