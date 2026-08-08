package org.example.simpleweibobackend.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklist {

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    public void add(String token, long expiryMillis) {
        blacklist.put(token, expiryMillis);
    }

    public boolean contains(String token) {
        Long expiry = blacklist.get(token);
        if (expiry == null) {
            return false;
        }
        if (System.currentTimeMillis() > expiry) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
