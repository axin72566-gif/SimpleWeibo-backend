package org.example.simpleweibobackend.vote.cache;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.vote.vo.VoteActivityVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class VoteActivityCache {

    private static final String KEY_PREFIX = "vote:activity:";
    private static final Duration TTL = Duration.ofHours(1);

    private final StringRedisTemplate stringRedisTemplate;

    public VoteActivityVO get(Long id) {
        String json = stringRedisTemplate.opsForValue().get(KEY_PREFIX + id);
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
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + vo.getId(), JSONUtil.toJsonStr(vo), TTL);
    }
}
