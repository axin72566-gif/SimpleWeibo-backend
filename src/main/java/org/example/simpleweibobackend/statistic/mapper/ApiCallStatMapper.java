package org.example.simpleweibobackend.statistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.simpleweibobackend.statistic.entity.ApiCallStat;

@Mapper
public interface ApiCallStatMapper extends BaseMapper<ApiCallStat> {

    @Insert("""
            INSERT INTO api_call_stat (api_path, http_method, controller_class, controller_method, call_count, last_call_time)
            VALUES (#{apiPath}, #{httpMethod}, #{controllerClass}, #{controllerMethod}, 1, NOW())
            ON DUPLICATE KEY UPDATE
                call_count = call_count + 1,
                last_call_time = NOW()
            """)
    int upsertCallCount(@Param("apiPath") String apiPath,
                        @Param("httpMethod") String httpMethod,
                        @Param("controllerClass") String controllerClass,
                        @Param("controllerMethod") String controllerMethod);
}
