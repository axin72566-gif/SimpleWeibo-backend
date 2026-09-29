package org.example.simpleweibobackend.user.logout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class LogoutController {

    private final StringRedisTemplate stringRedisTemplate;

    /** 退出登录 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        try {
            stringRedisTemplate.delete(UserContext.LOGIN_TOKEN + UserContext.getToken());
        } catch (Exception e) {
            log.error("退出登录失败, token 删除失败: token={}", UserContext.getToken(), e);
            throw new BizException(ErrorCode.LOGOUT_FAILED);
        }
        return Result.success(null);
    }
}
