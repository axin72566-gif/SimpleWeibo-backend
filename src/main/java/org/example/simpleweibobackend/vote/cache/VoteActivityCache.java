package org.example.simpleweibobackend.vote.cache;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.vote.vo.VoteActivityVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoteActivityCache {

    private static final String KEY_PREFIX = "vote:activity:";
    private static final Duration BASE_TTL = Duration.ofMinutes(60);
    private static final Duration TTL_JITTER = Duration.ofMinutes(10);

    private final StringRedisTemplate stringRedisTemplate;

    public VoteActivityVO get(Long id) {
        String json;
        try {
            json = stringRedisTemplate.opsForValue().get(KEY_PREFIX + id);
        } catch (Exception e) {
            // Redis故障时降级为未命中，由调用方回源数据库
            log.warn("读取活动缓存失败, id={}", id, e);
            return null;
        }
        if (json == null) {
            return null;
        }
        try {
            return JSONUtil.toBean(json, VoteActivityVO.class);
        } catch (JSONException e) {
            // 缓存数据损坏时视为未命中，由调用方回源数据库
            return null;
        }
    }

    public void put(VoteActivityVO vo) {
        // TTL带±10分钟随机抖动，避免大量活动同时过期造成缓存雪崩
        long jitter = ThreadLocalRandom.current().nextLong(-TTL_JITTER.getSeconds(), TTL_JITTER.getSeconds());
        Duration ttl = BASE_TTL.plusSeconds(jitter);
        String json = JSONUtil.toJsonStr(vo);
        try {
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + vo.getId(), json, ttl);
        } catch (Exception e) {
            // 缓存写入尽力而为，Redis故障不影响业务
            log.warn("写入活动缓存失败, id={}", vo.getId(), e);
        }
    }
}
