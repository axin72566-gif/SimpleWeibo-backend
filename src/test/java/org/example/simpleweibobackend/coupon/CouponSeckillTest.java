package org.example.simpleweibobackend.coupon;

import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.coupon.dto.CreateCouponRequest;
import org.example.simpleweibobackend.coupon.service.CouponService;
import org.example.simpleweibobackend.coupon.service.UserCouponService;
import org.example.simpleweibobackend.coupon.vo.CouponVO;
import org.example.simpleweibobackend.coupon.vo.UserCouponVO;
import org.example.simpleweibobackend.exception.BizException;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(properties = {
        "mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.nologging.NoLoggingImpl"
})
@Slf4j
class CouponSeckillTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TEST_PREFIX = "seckill_test_" + System.currentTimeMillis();
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
    void testSeckillBasicFlow() {
        Long couponId = createAndPublishCoupon(10,
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1));
        Long userId = createTestUser();

        UserContext.setUserId(userId);
        UserCouponVO vo = userCouponService.seckill(couponId);

        assertNotNull(vo);
        assertEquals(couponId, vo.getCouponId());
        assertEquals(userId, vo.getUserId());

        String redisStock = redisTemplate.opsForValue().get("coupon:stock:" + couponId);
        assertEquals("9", redisStock);

        awaitUserCouponPersisted(couponId, userId);

        Integer dbCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_coupon WHERE user_id = ? AND coupon_id = ?",
                Integer.class, userId, couponId);
        assertEquals(1, dbCount);

        Integer stockRemaining = jdbcTemplate.queryForObject(
                "SELECT stock_remaining FROM coupon WHERE id = ?",
                Integer.class, couponId);
        assertEquals(9, stockRemaining);

        UserContext.clear();
        log.info("基本秒杀流程测试通过");
    }

    @Test
    void testAntiOversell() throws Exception {
        int stock = 1;
        int threads = 100;

        Long couponId = createAndPublishCoupon(stock,
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1));
        List<Long> userIds = createTestUsers(threads);

        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final Long userId = userIds.get(i);
            executor.submit(() -> {
                try {
                    UserContext.setUserId(userId);
                    readyLatch.countDown();
                    startLatch.await();
                    userCouponService.seckill(couponId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    UserContext.clear();
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        long startMs = System.currentTimeMillis();
        startLatch.countDown();
        boolean completed = doneLatch.await(30, TimeUnit.SECONDS);
        long elapsedMs = System.currentTimeMillis() - startMs;
        executor.shutdown();

        log.info("防超卖测试: {}并发, 库存={}, 耗时={}ms", threads, stock, elapsedMs);

        assertEquals(true, completed, "并发请求未在30s内完成");
        assertEquals(stock, successCount.get(), "成功数应等于库存量");
        assertEquals(threads - stock, failCount.get(), "失败数应等于并发数减库存");

        String redisStock = redisTemplate.opsForValue().get("coupon:stock:" + couponId);
        assertEquals("0", redisStock, "Redis库存应归零");

        awaitTotalClaimed(couponId, stock);

        Integer dbCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM user_coupon WHERE coupon_id = ?",
                Integer.class, couponId);
        assertEquals(stock, dbCount, "MySQL落库数应等于库存量，无超卖");

        Integer stockRemaining = jdbcTemplate.queryForObject(
                "SELECT stock_remaining FROM coupon WHERE id = ?",
                Integer.class, couponId);
        assertEquals(0, stockRemaining, "MySQL库存应归零");

        log.info("防超卖测试通过: 成功={}, 失败={}", successCount.get(), failCount.get());
    }

    @Test
    void testAntiDuplicate() {
        Long couponId = createAndPublishCoupon(10,
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1));
        Long userId = createTestUser();

        UserContext.setUserId(userId);

        UserCouponVO vo = userCouponService.seckill(couponId);
        assertNotNull(vo);

        BizException ex = assertThrows(BizException.class,
                () -> userCouponService.seckill(couponId));
        assertEquals(ErrorCode.CONFLICT.getCode(), ex.getCode());

        UserContext.clear();
        log.info("防重复领取测试通过: 第二次秒杀被拒绝, code={}", ex.getCode());
    }

    @Test
    void testCouponNotFound() {
        Long userId = createTestUser();
        UserContext.setUserId(userId);

        BizException ex = assertThrows(BizException.class,
                () -> userCouponService.seckill(999999999L));
        assertEquals(ErrorCode.NOT_FOUND.getCode(), ex.getCode());

        UserContext.clear();
        log.info("优惠券不存在测试通过: code={}", ex.getCode());
    }

    @Test
    void testCouponNotPublished() {
        Long couponId = createCoupon(10,
                LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusDays(1));

        Long userId = createTestUser();
        UserContext.setUserId(userId);

        BizException ex = assertThrows(BizException.class,
                () -> userCouponService.seckill(couponId));
        assertEquals(ErrorCode.BAD_REQUEST.getCode(), ex.getCode());

        UserContext.clear();
        log.info("优惠券未上架测试通过: code={}", ex.getCode());
    }

    @Test
    void testActivityNotStarted() {
        Long couponId = createAndPublishCoupon(10,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        Long userId = createTestUser();
        UserContext.setUserId(userId);

        BizException ex = assertThrows(BizException.class,
                () -> userCouponService.seckill(couponId));
        assertEquals(ErrorCode.BAD_REQUEST.getCode(), ex.getCode());

        UserContext.clear();
        log.info("活动尚未开始测试通过: code={}", ex.getCode());
    }

    @Test
    void testActivityEnded() {
        Long couponId = createAndPublishCoupon(10,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        Long userId = createTestUser();
        UserContext.setUserId(userId);

        BizException ex = assertThrows(BizException.class,
                () -> userCouponService.seckill(couponId));
        assertEquals(ErrorCode.BAD_REQUEST.getCode(), ex.getCode());

        UserContext.clear();
        log.info("活动已结束测试通过: code={}", ex.getCode());
    }

    private Long createCoupon(int quantity, LocalDateTime startTime, LocalDateTime endTime) {
        CreateCouponRequest request = new CreateCouponRequest();
        request.setName(TEST_PREFIX + "_coupon");
        request.setDiscountRate(80);
        request.setTotalQuantity(quantity);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        CouponVO vo = couponService.create(request);
        createdCouponIds.add(vo.getId());
        return vo.getId();
    }

    private Long createAndPublishCoupon(int quantity, LocalDateTime startTime, LocalDateTime endTime) {
        Long couponId = createCoupon(quantity, startTime, endTime);
        couponService.publish(couponId);
        return couponId;
    }

    private Long createTestUser() {
        String username = TEST_PREFIX + "_user_" + System.nanoTime();
        String hashedPassword = PasswordUtil.hash("test123");
        jdbcTemplate.update("INSERT INTO user (username, password) VALUES (?, ?)", username, hashedPassword);
        Long id = jdbcTemplate.queryForObject("SELECT id FROM user WHERE username = ?", Long.class, username);
        createdUserIds.add(id);
        return id;
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

    private void awaitUserCouponPersisted(Long couponId, Long userId) {
        int maxPoll = 100;
        for (int i = 0; i < maxPoll; i++) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user_coupon WHERE user_id = ? AND coupon_id = ?",
                    Integer.class, userId, couponId);
            if (count != null && count > 0) {
                return;
            }
            sleep(100);
        }
        log.warn("等待MQ消费超时: couponId={}, userId={}", couponId, userId);
    }

    private void awaitTotalClaimed(Long couponId, int expectedCount) {
        int maxPoll = 100;
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
}
