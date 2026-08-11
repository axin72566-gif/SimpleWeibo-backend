package org.example.simpleweibobackend.statistic.controller;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.statistic.service.ApiCallStatService;
import org.example.simpleweibobackend.statistic.vo.ApiCallStatVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class ApiCallStatController {

    private final ApiCallStatService apiCallStatService;

    @GetMapping
    public Result<PageVO<ApiCallStatVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(apiCallStatService.list(page, size));
    }
}
