package org.example.simpleweibobackend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.dto.RegisterRequest;
import org.example.simpleweibobackend.user.service.UserService;
import org.example.simpleweibobackend.user.vo.RegisterVO;
import org.example.simpleweibobackend.user.vo.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<RegisterVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }

    // 无登录鉴权，用户身份通过 X-User-Id 请求头传入（见 UserIdInterceptor）

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(userService.getCurrentUser());
    }
}
