package org.example.simpleweibobackend.coupon;

import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.coupon.dto.CreateCouponRequest;
import org.example.simpleweibobackend.coupon.service.CouponService;
import org.example.simpleweibobackend.coupon.service.UserCouponService;
import org.example.simpleweibobackend.coupon.vo.CouponVO;
import org.example.simpleweibobackend.coupon.vo.UserCouponVO;
import org.example.simpleweibobackend.util.PasswordUtil;
import org.example.simpleweibobackend.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@SpringBootTest
@TestPropertySource(properties = {
        "mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.nologging.NoLoggingImpl"
})
@Slf4j
class CouponSeckillBenchmarkTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TEST_PREFIX = "seckill_bench_" + System.currentTimeMillis();
    private final List<Long> createdCouponIds = new ArrayList<>();
    private final List<Long> createdUserIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        ensureSchema();
    }

    @AfterEach
    void cleanup() {
        cleanupTestData();
    }

    @Test
    void benchmarkSeckillQps() throws Exception {
        int[] concurrencyLevels = {100, 500, 1000, 2000};

        log.info("=== 秒杀接口 QPS 基准测试 ===");
        log.info("环境: MySQL本地, Redis本地(连接池max-active=50), RabbitMQ本地");
        log.info("热路径: Redis Hash读(缓存) + Redis Lua(库存扣减) + MQ发送");
        log.info("");
        log.info("| 并发数  | 总请求 | 成功数 | 失败数 | 总耗时(ms) | QPS    | P50(ms) | P95(ms) | P99(ms) | 消费耗时(ms) |");
        log.info("|---------|--------|--------|--------|-----------|--------|---------|---------|---------|------------|");

        for (int concurrency : concurrencyLevels) {
            try {
                BenchmarkResult result = runBenchmark(concurrency);
                log.info(String.format("| %-7d | %-6d | %-6d | %-6d | %-9d | %-6d | %-7d | %-7d | %-7d | %-10d |",
                        concurrency, concurrency, result.successCount(), result.failCount(),
                        result.totalTimeMs(), result.qps(), result.p50Ms(), result.p95Ms(), result.p99Ms(),
                        result.consumerTimeMs()));
            } catch (Exception e) {
                log.error(String.format("| %-7d | 测试失败: %s", concurrency, e.getMessage()), e);
            }
            cleanupTestData();
        }
    }

    private BenchmarkResult runBenchmark(int concurrency) throws Exception {
        Long couponId = createAndPublishCoupon(concurrency);
        log.info("准备数据: 优惠券库存={}, 创建{}个用户...", concurrency, concurrency);

        List<Long> userIds = createTestUsers(concurrency);

        CountDownLatch readyLatch = new CountDownLatch(concurrency);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(concurrency);
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);

        long[] latencies = new long[concurrency];
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < concurrency; i++) {
            final int idx = i;
            final Long userId = userIds.get(i);
            executor.submit(() -> {
                try {
                    UserContext.setUserId(userId);
                    readyLatch.countDown();
                    startLatch.await();
                    long start = System.nanoTime();
                    UserCouponVO vo = userCouponService.seckill(couponId);
                    latencies[idx] = (System.nanoTime() - start) / 1_000_000;
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    latencies[idx] = -1;
                    failCount.incrementAndGet();
                } finally {
                    UserContext.clear();
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        long totalStart = System.nanoTime();
        startLatch.countDown();
        boolean completed = doneLatch.await(120, TimeUnit.SECONDS);
        long totalTime = (System.nanoTime() - totalStart) / 1_000_000;
        executor.shutdownNow();

        if (!completed) {
            log.warn("并发={} 超时(120s)，部分请求未完成", concurrency);
        }

        List<Long> validLatencies = new ArrayList<>();
        for (long l : latencies) {
            if (l >= 0) {
                validLatencies.add(l);
            }
        }
        Collections.sort(validLatencies);

        long p50 = validLatencies.isEmpty() ? -1 : validLatencies.get(validLatencies.size() / 2);
        long p95 = validLatencies.isEmpty() ? -1
                : validLatencies.get(Math.min((int) (validLatencies.size() * 0.95), validLatencies.size() - 1));
        long p99 = validLatencies.isEmpty() ? -1 : validLatencies.get(validLatencies.size() - 1);

        long qps = totalTime > 0 ? (long) ((double) successCount.get() / totalTime * 1000) : 0;

        long consumerStart = System.nanoTime();
        awaitTotalClaimed(couponId, successCount.get());
        long consumerTime = (System.nanoTime() - consumerStart) / 1_000_000;

        log.info("并发={}: 成功={}, 失败={}, QPS={}, P50={}ms, P99={}ms, 消费耗时={}ms",
                concurrency, successCount.get(), failCount.get(), qps, p50, p99, consumerTime);

        return new BenchmarkResult(totalTime, successCount.get(), failCount.get(), qps, p50, p95, p99, consumerTime);
    }

    private Long createAndPublishCoupon(int quantity) {
        CreateCouponRequest request = new CreateCouponRequest();
        request.setName(TEST_PREFIX + "_coupon");
        request.setDiscountRate(80);
        request.setTotalQuantity(quantity);
        request.setStartTime(LocalDateTime.now().minusMinutes(1));
        request.setEndTime(LocalDateTime.now().plusDays(1));
        CouponVO vo = couponService.create(request);
        couponService.publish(vo.getId());
        createdCouponIds.add(vo.getId());
        return vo.getId();
    }

    private List<Long> createTestUsers(int count) {
        long batchId = System.nanoTime();
        String fullPrefix = TEST_PREFIX + "_u" + batchId + "_";
        String hashedPassword = PasswordUtil.hash("test123");
        jdbcTemplate.batchUpdate("INSERT INTO user (username, password) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, fullPrefix + i);
                        ps.setString(2, hashedPassword);
                    }

                    @Override
                    public int getBatchSize() {
                        return count;
                    }
                });
        List<Long> ids = jdbcTemplate.queryForList(
                "SELECT id FROM user WHERE username LIKE ? ORDER BY id", Long.class, fullPrefix + "%");
        createdUserIds.addAll(ids);
        return ids;
    }

    private void awaitTotalClaimed(Long couponId, int expectedCount) {
        int maxPoll = 600;
        for (int i = 0; i < maxPoll; i++) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_coupon WHERE coupon_id = ?",
                    Integer.class, couponId);
            if (count != null && count >= expectedCount) {
                return;
            }
            sleep(100);
        }
        log.warn("等待MQ消费超时: couponId={}, expected={}", couponId, expectedCount);
    }

    private void ensureSchema() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_coupon (" +
                "id bigint unsigned auto_increment primary key comment '主键ID', " +
                "user_id bigint not null comment '用户ID', " +
                "coupon_id bigint not null comment '优惠券ID', " +
                "status varchar(20) not null default 'UNUSED' comment '状态', " +
                "create_time datetime default CURRENT_TIMESTAMP null comment '领取时间', " +
                "constraint uk_user_coupon unique (user_id, coupon_id)" +
                ") collate = utf8mb4_unicode_ci");
        try {
            jdbcTemplate.execute("ALTER TABLE coupon ADD COLUMN stock_remaining int NOT NULL DEFAULT 0 COMMENT '剩余库存' AFTER total_quantity");
        } catch (Exception ignored) {
        }
    }

    private void cleanupTestData() {
        for (Long couponId : createdCouponIds) {
            redisTemplate.delete("coupon:stock:" + couponId);
            redisTemplate.delete("coupon:user:" + couponId);
            redisTemplate.delete("coupon:info:" + couponId);
        }
        if (!createdCouponIds.isEmpty()) {
            String couponIds = createdCouponIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            jdbcTemplate.execute("DELETE FROM user_coupon WHERE coupon_id IN (" + couponIds + ")");
            jdbcTemplate.execute("DELETE FROM coupon WHERE id IN (" + couponIds + ")");
        }
        if (!createdUserIds.isEmpty()) {
            String userIds = createdUserIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            jdbcTemplate.execute("DELETE FROM user_coupon WHERE user_id IN (" + userIds + ")");
            jdbcTemplate.execute("DELETE FROM user WHERE id IN (" + userIds + ")");
        }
        createdCouponIds.clear();
        createdUserIds.clear();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private record BenchmarkResult(long totalTimeMs, int successCount, int failCount,
                                   long qps, long p50Ms, long p95Ms, long p99Ms, long consumerTimeMs) {
    }
}
