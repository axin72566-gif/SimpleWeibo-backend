package org.example.simpleweibobackend.user.logout;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class LogoutController {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 退出登录:删除 Redis 中的登录态,token 立即失效
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        stringRedisTemplate.delete(UserContext.TOKEN_KEY_PREFIX + UserContext.getToken());
        return Result.success(null);
    }
}
