package org.example.simpleweibobackend.user.auth;

import java.time.Duration;

/** 登录态公共定义 */
public final class UserContext {

    /** 登录态 key 前缀 */
    public static final String LOGIN_TOKEN = "user:login:token:";

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
