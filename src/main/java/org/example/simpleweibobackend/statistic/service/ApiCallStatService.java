package org.example.simpleweibobackend.statistic.service;

import org.example.simpleweibobackend.statistic.vo.ApiCallStatVO;

import java.util.List;

public interface ApiCallStatService {

    void recordCall(String apiPath, String httpMethod, String controllerClass, String controllerMethod);

    List<ApiCallStatVO> list();
}
