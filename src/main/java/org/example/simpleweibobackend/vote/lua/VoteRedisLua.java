package org.example.simpleweibobackend.vote.lua;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class VoteRedisLua {

    @Bean
    public DefaultRedisScript<Long> castVoteScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
                local oldPostId = redis.call('HGET', KEYS[1], ARGV[1])
                if oldPostId then
                    if oldPostId == ARGV[2] then
                        return 2
                    end
                    return 0
                end
                redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
                redis.call('HINCRBY', KEYS[2], ARGV[2], 1)
                redis.call('SADD', KEYS[3], ARGV[1])
                return 1
                """);
        script.setResultType(Long.class);
        return script;
    }
}
