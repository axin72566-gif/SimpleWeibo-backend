package org.example.simpleweibobackend.statistic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.RequireRole;
import org.example.simpleweibobackend.common.Role;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.statistic.service.ApiCallStatService;
import org.example.simpleweibobackend.statistic.vo.ApiCallStatVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequireRole(Role.ADMIN)
@Tag(name = "接口调用统计")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class ApiCallStatController {

    private final ApiCallStatService apiCallStatService;

    @Operation(summary = "分页查询接口调用统计")
    @GetMapping
    public Result<PageVO<ApiCallStatVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        return Result.success(apiCallStatService.list(page, size));
    }
}
