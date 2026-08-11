package org.example.simpleweibobackend.statistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.simpleweibobackend.statistic.dto.CallStatDelta;
import org.example.simpleweibobackend.statistic.entity.ApiCallStat;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ApiCallStatMapper extends BaseMapper<ApiCallStat> {

    @Insert("""
            <script>
            INSERT INTO api_call_stat (api_path, http_method, controller_class, controller_method, call_count, last_call_time)
            VALUES
            <foreach collection="deltas" item="d" separator=",">
                (#{d.apiPath}, #{d.httpMethod}, #{d.controllerClass}, #{d.controllerMethod}, #{d.count}, #{d.lastCallTime})
            </foreach>
            ON DUPLICATE KEY UPDATE
                call_count = call_count + VALUES(call_count),
                last_call_time = VALUES(last_call_time),
                controller_class = VALUES(controller_class),
                controller_method = VALUES(controller_method)
            </script>
            """)
    int batchUpsertCallCount(@Param("deltas") List<CallStatDelta> deltas);
}
