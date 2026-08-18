package org.example.simpleweibobackend.feed;

import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.service.PostService;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.example.simpleweibobackend.util.PasswordUtil;
import org.example.simpleweibobackend.util.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest
@TestPropertySource(properties = {
        "mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.nologging.NoLoggingImpl"
})
@Slf4j
class FeedFanoutBenchmarkTest {

    @Autowired
    private PostService postService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String TEST_PREFIX = "bench_" + System.currentTimeMillis();
    private final List<Long> currentUserIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        cleanupConfigData();
    }

    @Test
    void benchmarkFeedFanout() throws Exception {
        // 确保依赖的表和索引存在
        ensureSchema();

        log.info("=== Feed Fanout 基准测试 (线程池异步模式) ===");
        log.info("环境: HikariCP连接池=50, Tomcat线程=200(默认), MySQL本地单实例, 本地线程池异步fanout(4核心/16最大/队列1000)");
        log.info("");

        int[][] configs = {
                {10, 10},
                {10, 50},
                {10, 100},
                {10, 500},
                {10, 1000},
                {20, 100},
                {20, 500},
                {50, 100},
                {50, 500},
                {100, 100},
        };

        log.info("| m   | n    | 发帖(ms) | 成功率   | 平均(ms) | P50(ms) | P95(ms) | P99(ms) | 消费(ms) | inbox行数 | 预期行数 |");
        log.info("|-----|------|---------|---------|---------|---------|---------|---------|---------|---------|---------|");

        for (int[] config : configs) {
            int m = config[0];
            int n = config[1];
            try {
                BenchmarkResult result = runBenchmark(m, n);
                int expected = result.successCount() * (n + 1);
                log.info(String.format("| %-3d | %-4d | %-7d | %-7s | %-7d | %-7d | %-7d | %-7d | %-7d | %-7d | %-7d |",
                        m, n, result.totalTimeMs(),
                        result.successCount() + "/" + m,
                        result.avgMs(), result.p50Ms(), result.p95Ms(), result.p99Ms(),
                        result.consumerTimeMs(), result.feedItemCount(), expected));
            } catch (Exception e) {
                log.error(String.format("| %-3d | %-4d | 测试失败: %s", m, n, e.getMessage()));
            }
            cleanupConfigData();
        }
    }

    private BenchmarkResult runBenchmark(int m, int n) throws Exception {
        // 1. 创建 m 个博主 + n 个共享粉丝
        long setupStart = System.currentTimeMillis();
        List<Long> bloggerIds = createUsers(m, "blogger");
        List<Long> followerIds = createUsers(n, "follower");
        createFollows(followerIds, bloggerIds);
        long setupTime = System.currentTimeMillis() - setupStart;
        log.info("准备数据: m={}, n={}, {}用户+{}关注关系, 耗时{}ms",
                m, n, m + n, m * n, setupTime);

        // 2. m 个博主同时发帖
        CountDownLatch readyLatch = new CountDownLatch(m);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(m);
        ExecutorService executor = Executors.newFixedThreadPool(m);

        long[] latencies = new long[m];
        boolean[] successes = new boolean[m];
        List<Long> createdPostIds = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < m; i++) {
            final int idx = i;
            final Long bloggerId = bloggerIds.get(i);
            executor.submit(() -> {
                try {
                    UserContext.setUserId(bloggerId);
                    CreatePostRequest req = new CreatePostRequest();
                    req.setTitle("benchmark-" + idx);
                    req.setContent("benchmark post by blogger " + bloggerId);
                    readyLatch.countDown();
                    startLatch.await();
                    long start = System.nanoTime();
                    PostVO vo = postService.createPost(req);
                    latencies[idx] = (System.nanoTime() - start) / 1_000_000;
                    successes[idx] = true;
                    createdPostIds.add(vo.getId());
                } catch (Exception e) {
                    latencies[idx] = -1;
                    successes[idx] = false;
                    log.warn("blogger {} 发帖失败: {}", bloggerId, e.getMessage());
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
            log.warn("m={}, n={}: 超时(120s)，部分请求未完成", m, n);
        }

        // 3. 统计延迟
        int successCount = 0;
        List<Long> validLatencies = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            if (successes[i]) {
                successCount++;
                validLatencies.add(latencies[i]);
            }
        }
        Collections.sort(validLatencies);

        long avg = validLatencies.isEmpty() ? -1
                : validLatencies.stream().mapToLong(l -> l).sum() / validLatencies.size();
        long p50 = validLatencies.isEmpty() ? -1 : validLatencies.get(validLatencies.size() / 2);
        long p95 = validLatencies.isEmpty() ? -1
                : validLatencies.get(Math.min((int) (validLatencies.size() * 0.95), validLatencies.size() - 1));
        long p99 = validLatencies.isEmpty() ? -1 : validLatencies.get(validLatencies.size() - 1);

        // 4. 等待 Consumer 异步消费完成，轮询 inbox 行数
        long expectedFeedCount = (long) successCount * (n + 1);
        long feedCount = 0;
        long consumerStart = System.nanoTime();
        int maxPollRounds = 600; // 60s 超时 (100ms * 600)
        int pollRounds = 0;
        while (pollRounds < maxPollRounds) {
            if (!createdPostIds.isEmpty()) {
                String postIds = createdPostIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                feedCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM inbox WHERE post_id IN (" + postIds + ")", Long.class);
            }
            if (feedCount >= expectedFeedCount) {
                break;
            }
            Thread.sleep(100);
            pollRounds++;
        }
        long consumerTime = (System.nanoTime() - consumerStart) / 1_000_000;
        if (feedCount < expectedFeedCount) {
            log.warn("m={}, n={}: Consumer 未在 60s 内完成消费, feed={}/{}", m, n, feedCount, expectedFeedCount);
        }

        return new BenchmarkResult(totalTime, successCount, avg, p50, p95, p99, feedCount, consumerTime);
    }

    private List<Long> createUsers(int count, String prefix) {
        String fullPrefix = TEST_PREFIX + "_" + prefix + "_";
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
        currentUserIds.addAll(ids);
        return ids;
    }

    private void createFollows(List<Long> followerIds, List<Long> bloggerIds) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO follow (follower_id, following_id) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        int followerIdx = i / bloggerIds.size();
                        int bloggerIdx = i % bloggerIds.size();
                        ps.setLong(1, followerIds.get(followerIdx));
                        ps.setLong(2, bloggerIds.get(bloggerIdx));
                    }

                    @Override
                    public int getBatchSize() {
                        return followerIds.size() * bloggerIds.size();
                    }
                });
    }

    private void ensureSchema() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS inbox (" +
                "id bigint unsigned auto_increment primary key comment '收件箱记录ID', " +
                "user_id bigint unsigned not null comment '收件人用户ID', " +
                "post_id bigint unsigned not null comment '帖子ID', " +
                "post_user_id bigint unsigned not null comment '发帖人用户ID', " +
                "create_time datetime default CURRENT_TIMESTAMP null comment '创建时间', " +
                "update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间', " +
                "index idx_user_create (user_id, create_time desc)" +
                ") collate = utf8mb4_unicode_ci");
        try {
            jdbcTemplate.execute("CREATE INDEX idx_following_follower ON follow(following_id, follower_id)");
        } catch (Exception ignored) {
            // 索引已存在
        }
        try {
            jdbcTemplate.execute("ALTER TABLE inbox ADD CONSTRAINT uk_user_post UNIQUE (user_id, post_id)");
        } catch (Exception ignored) {
            // 唯一约束已存在
        }
    }

    private void cleanupConfigData() {
        if (currentUserIds.isEmpty()) {
            return;
        }
        String ids = currentUserIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        jdbcTemplate.execute("DELETE FROM inbox WHERE user_id IN (" + ids + ") OR post_user_id IN (" + ids + ")");
        jdbcTemplate.execute("DELETE FROM post WHERE user_id IN (" + ids + ")");
        jdbcTemplate.execute("DELETE FROM follow WHERE follower_id IN (" + ids + ") OR following_id IN (" + ids + ")");
        jdbcTemplate.execute("DELETE FROM user WHERE id IN (" + ids + ")");
        currentUserIds.clear();
    }

    private record BenchmarkResult(long totalTimeMs, int successCount, long avgMs,
                                   long p50Ms, long p95Ms, long p99Ms, long feedItemCount,
                                   long consumerTimeMs) {
    }
}
