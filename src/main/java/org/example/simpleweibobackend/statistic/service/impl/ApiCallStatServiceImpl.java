package org.example.simpleweibobackend.statistic.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.statistic.entity.ApiCallStat;
import org.example.simpleweibobackend.statistic.mapper.ApiCallStatMapper;
import org.example.simpleweibobackend.statistic.service.ApiCallStatService;
import org.example.simpleweibobackend.statistic.vo.ApiCallStatVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiCallStatServiceImpl implements ApiCallStatService {

    private final ApiCallStatMapper apiCallStatMapper;

    @Override
    public void recordCall(String apiPath, String httpMethod, String controllerClass, String controllerMethod) {
        apiCallStatMapper.upsertCallCount(apiPath, httpMethod, controllerClass, controllerMethod);
    }

    @Override
    public List<ApiCallStatVO> list() {
        return apiCallStatMapper.selectList(null).stream()
                .map(ApiCallStatVO::from)
                .toList();
    }
}
