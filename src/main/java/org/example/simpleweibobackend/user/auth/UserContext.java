package org.example.simpleweibobackend.user.auth;

/**
 * 当前请求的登录态,由 {@link AuthInterceptor} 校验通过后写入,
 * 业务代码通过它读取 userId/token,请求结束后由拦截器清理,防止线程复用串号
 */
public final class UserContext {

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
