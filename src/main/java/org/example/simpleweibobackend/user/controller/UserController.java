package org.example.simpleweibobackend.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.dto.RegisterRequest;
import org.example.simpleweibobackend.user.service.UserService;
import org.example.simpleweibobackend.user.vo.RegisterVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<RegisterVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }
}
