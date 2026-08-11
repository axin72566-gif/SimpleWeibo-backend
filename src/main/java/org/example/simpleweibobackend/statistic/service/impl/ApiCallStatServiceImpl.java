package org.example.simpleweibobackend.statistic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.statistic.dto.CallStatDelta;
import org.example.simpleweibobackend.statistic.entity.ApiCallStat;
import org.example.simpleweibobackend.statistic.mapper.ApiCallStatMapper;
import org.example.simpleweibobackend.statistic.service.ApiCallStatService;
import org.example.simpleweibobackend.statistic.vo.ApiCallStatVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiCallStatServiceImpl implements ApiCallStatService {

    private static final int BATCH_SIZE = 200;

    private final ApiCallStatMapper apiCallStatMapper;

    @Override
    public void flushBatch(List<CallStatDelta> deltas) {
        if (deltas.isEmpty()) {
            return;
        }
        for (int i = 0; i < deltas.size(); i += BATCH_SIZE) {
            List<CallStatDelta> batch = deltas.subList(i, Math.min(i + BATCH_SIZE, deltas.size()));
            apiCallStatMapper.batchUpsertCallCount(batch);
        }
    }

    @Override
    public PageVO<ApiCallStatVO> list(int page, int size) {
        Page<ApiCallStat> pageParam = new Page<>(page, size);
        QueryWrapper<ApiCallStat> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("call_count");
        Page<ApiCallStat> result = apiCallStatMapper.selectPage(pageParam, wrapper);
        List<ApiCallStatVO> records = result.getRecords().stream()
                .map(ApiCallStatVO::from)
                .toList();
        return PageVO.of(records, result.getTotal(), page, size);
    }
}
