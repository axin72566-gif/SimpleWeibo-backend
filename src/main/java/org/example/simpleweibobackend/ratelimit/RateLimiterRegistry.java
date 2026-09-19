package org.example.simpleweibobackend.ratelimit;

import org.example.simpleweibobackend.ratelimit.limiter.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 限流算法注册表:启动时自动收集容器中所有 {@link RateLimiter} 实现,
 * 按 {@link RateLimiter#algorithm()} 声明的算法建立映射。
 * 新增算法只需新增一个 @Component 实现类,本类与切面无需任何改动
 */
@Component
public class RateLimiterRegistry {

    private final Map<RateLimitAlgorithm, RateLimiter> limiters;

    /**
     * @param limiters Spring 注入的所有 RateLimiter 实现
     * @throws IllegalStateException 两个实现声明了同一种算法时启动失败
     */
    public RateLimiterRegistry(List<RateLimiter> limiters) {
        this.limiters = limiters.stream()
                .collect(Collectors.toMap(
                        RateLimiter::algorithm,
                        Function.identity(),
                        (a, b) -> {
                            throw new IllegalStateException("重复的限流算法实现: " + a.algorithm());
                        },
                        () -> new EnumMap<>(RateLimitAlgorithm.class)));
    }

    /**
     * 按算法类型获取对应的限流器
     *
     * @throws IllegalStateException 算法没有任何实现时抛出
     */
    public RateLimiter get(RateLimitAlgorithm algorithm) {
        RateLimiter limiter = limiters.get(algorithm);
        if (limiter == null) {
            throw new IllegalStateException("未注册的限流算法: " + algorithm);
        }
        return limiter;
    }
}
