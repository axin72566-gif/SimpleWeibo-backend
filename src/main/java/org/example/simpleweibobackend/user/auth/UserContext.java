package org.example.simpleweibobackend.user.auth;

import java.time.Duration;

/**
 * 登录态的公共定义:Redis 中 token 的 key 规则与有效期常量,
 * 以及当前线程的用户信息(由 AuthInterceptor 校验通过后写入,请求结束清理,防止线程复用串号)
 */
public final class UserContext {

    /**
     * Redis 中登录态 key 的前缀,实际 key 为该前缀 + token
     */
    public static final String TOKEN_KEY_PREFIX = "simpleweibo:login:token:";

    /**
     * token 有效期 7 天
     */
    public static final Duration TOKEN_TTL = Duration.ofDays(7);

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId, String token) {
        USER_ID.set(userId);
        TOKEN.set(token);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static String getToken() {
        return TOKEN.get();
    }

    public static void clear() {
        USER_ID.remove();
        TOKEN.remove();
    }
}
