package org.example.simpleweibobackend.statistic.service;

import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.statistic.dto.CallStatDelta;
import org.example.simpleweibobackend.statistic.vo.ApiCallStatVO;

import java.util.List;

public interface ApiCallStatService {

    void flushBatch(List<CallStatDelta> deltas);

    PageVO<ApiCallStatVO> list(int page, int size);
}
