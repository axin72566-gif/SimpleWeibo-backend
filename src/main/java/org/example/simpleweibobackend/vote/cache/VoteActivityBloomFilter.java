package org.example.simpleweibobackend.vote.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.vote.entity.VoteActivity;
import org.example.simpleweibobackend.vote.mapper.VoteActivityMapper;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLongArray;

@Component
@RequiredArgsConstructor
public class VoteActivityBloomFilter implements ApplicationRunner {

    private static final long SEED_1 = 0x9E3779B97F4A7C15L;
    private static final long SEED_2 = 0xBF58476D1CE4E5B9L;

    /**
     * 2^24 个比特 + 7 个哈希，约170万个ID内误判率约1%
     */
    private static final int BITS = 1 << 24;
    private static final int HASH_COUNT = 7;

    private final AtomicLongArray words = new AtomicLongArray((BITS + 63) >> 6);
    private final VoteActivityMapper voteActivityMapper;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        // 过滤器在JVM内存里，每次启动都需要用库中已有ID重新灌入
        voteActivityMapper.selectObjs(new LambdaQueryWrapper<VoteActivity>().select(VoteActivity::getId))
                .forEach(id -> add(((Number) id).longValue()));
    }

    public void add(Long id) {
        for (int index : indexes(id)) {
            int wordIndex = index >> 6;
            long mask = 1L << (index & 63);
            words.updateAndGet(wordIndex, word -> word | mask);
        }
    }

    public boolean mightContain(Long id) {
        for (int index : indexes(id)) {
            long word = words.get(index >> 6);
            if ((word & (1L << (index & 63))) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Kirsch-Mitzenmacher双重哈希：由两个独立哈希 h1、h2 线性组合出 HASH_COUNT 个下标
     */
    private int[] indexes(Long id) {
        long h1 = mix(id, SEED_1);
        long h2 = mix(id, SEED_2);
        int[] indexes = new int[HASH_COUNT];
        for (int i = 0; i < HASH_COUNT; i++) {
            indexes[i] = Math.floorMod(h1 + i * h2, BITS);
        }
        return indexes;
    }

    /**
     * Murmur3 64位 finalizer 混合
     */
    private long mix(long value, long seed) {
        long h = value + seed;
        h ^= h >>> 33;
        h *= 0xff51afd7ed558ccdL;
        h ^= h >>> 33;
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= h >>> 33;
        return h;
    }
}
