package org.example.simpleweibobackend.coupon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class SeckillRedisLuaConfig {

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

    /**
     * 秒杀补偿回滚脚本：仅在用户仍持有领取标记时才回滚（SREM + INCR），天然幂等，
     * 重复调用不会多加库存；stock 键已丢失时不 INCR，避免在 Redis 数据缺失场景下制造错误基数。
     * 返回 1=已回滚，0=无标记可回滚（跳过）
     */
    @Bean
    public DefaultRedisScript<Long> seckillRollbackScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
                if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 0 then return 0 end
                redis.call('SREM', KEYS[2], ARGV[1])
                if redis.call('EXISTS', KEYS[1]) == 1 then redis.call('INCR', KEYS[1]) end
                return 1
                """);
        script.setResultType(Long.class);
        return script;
    }
}
