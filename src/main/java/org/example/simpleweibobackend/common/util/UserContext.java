package org.example.simpleweibobackend.common.util;

public final class UserContext {

    private static final Long DEFAULT_USER_ID = 1L;

    private UserContext() {
    }

    public static Long getUserId() {
        return DEFAULT_USER_ID;
    }
}
