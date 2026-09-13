package org.example.simpleweibobackend.user.register;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.common.ratelimit.RateLimit;
import org.example.simpleweibobackend.common.ratelimit.RateLimitAlgorithm;
import org.example.simpleweibobackend.common.ratelimit.RateLimitDimension;
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
     * 限流:按 IP 滑动窗口,60 秒内最多 5 次,防止恶意批量注册
     *
     * @param request 注册请求(用户名 + 密码)
     * @return 注册成功的用户信息
     */
    @RateLimit(limit = 5, window = 60,
            dimension = RateLimitDimension.IP,
            algorithm = RateLimitAlgorithm.SLIDING_WINDOW,
            message = "注册请求过于频繁,请稍后再试")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(registerService.register(request));
    }
}
