package org.example.simpleweibobackend.user.register;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.UserVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户注册接口
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    /**
     * 注册新用户。
     *
     * @param request 注册请求(用户名 + 密码)
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(registerService.register(request));
    }
}
