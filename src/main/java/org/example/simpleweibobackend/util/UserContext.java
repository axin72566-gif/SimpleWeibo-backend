package org.example.simpleweibobackend.util;

import org.example.simpleweibobackend.user.constant.Role;

public final class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Role> CURRENT_ROLE = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Long getUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void setRole(Role role) {
        CURRENT_ROLE.set(role);
    }

    public static Role getRole() {
        return CURRENT_ROLE.get();
    }

    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_ROLE.remove();
    }
}
