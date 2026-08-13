package org.example.simpleweibobackend.outbox.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.simpleweibobackend.outbox.entity.Outbox;

import java.util.List;

@Mapper
public interface OutboxMapper extends BaseMapper<Outbox> {

    @Select("SELECT * FROM outbox WHERE status = 'PENDING' ORDER BY id LIMIT #{limit}")
    List<Outbox> selectPending(@Param("limit") int limit);

    @Update("UPDATE outbox SET status = 'SENT' WHERE id = #{id} AND status = 'PENDING'")
    int markSent(@Param("id") Long id);
}
