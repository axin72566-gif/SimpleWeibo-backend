package org.example.simpleweibobackend.common.util;

import org.springframework.data.redis.core.script.DigestUtils;

public class PasswordUtil {

    private static final String SALT = "simple_weibo_2026";

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        return DigestUtils.sha1DigestAsHex(rawPassword + SALT);
    }
}
