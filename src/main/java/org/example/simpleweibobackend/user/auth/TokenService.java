package org.example.simpleweibobackend.user.auth;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 登录态 token 管理:token 为随机串,userId 存在 Redis 服务端,
 * 退出登录删除 key 即可立即失效
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String TOKEN_KEY_PREFIX = "simpleweibo:login:token:";

    /**
     * token 有效期 7 天
     */
    private static final Duration TOKEN_TTL = Duration.ofDays(7);

    private final StringRedisTemplate stringRedisTemplate;

    public String createToken(Long userId) {
        String token = IdUtil.fastSimpleUUID();
        stringRedisTemplate.opsForValue().set(TOKEN_KEY_PREFIX + token, String.valueOf(userId), TOKEN_TTL);
        return token;
    }

    /**
     * @return 有效 token 对应的 userId,token 无效或已过期返回 null
     */
    public Long getUserIdByToken(String token) {
        String userId = stringRedisTemplate.opsForValue().get(TOKEN_KEY_PREFIX + token);
        return userId == null ? null : Long.valueOf(userId);
    }

    public void removeToken(String token) {
        stringRedisTemplate.delete(TOKEN_KEY_PREFIX + token);
    }
}
