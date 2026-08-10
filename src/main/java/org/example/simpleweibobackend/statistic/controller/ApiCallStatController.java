package org.example.simpleweibobackend.statistic.controller;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.statistic.service.ApiCallStatService;
import org.example.simpleweibobackend.statistic.vo.ApiCallStatVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class ApiCallStatController {

    private final ApiCallStatService apiCallStatService;

    @GetMapping
    public Result<List<ApiCallStatVO>> list() {
        return Result.success(apiCallStatService.list());
    }
}
