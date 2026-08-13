package org.example.simpleweibobackend.coupon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class SeckillRedisConfig {

    @Bean
    public DefaultRedisScript<Long> seckillScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
                local stock = redis.call('GET', KEYS[1])
                if not stock then return -1 end
                if tonumber(stock) <= 0 then return 1 end
                if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then return 2 end
                redis.call('DECR', KEYS[1])
                redis.call('SADD', KEYS[2], ARGV[1])
                return 0
                """);
        script.setResultType(Long.class);
        return script;
    }
}
