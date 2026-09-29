package org.example.simpleweibobackend.post.like;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 点赞压测:50 个线程同时点赞,每人 200 次,统计 QPS 和 P99 延迟,
 * 最后等 Kafka 消费端全部落库,验证一万次点赞一条不丢。
 * 帖子是测试自己建的、结束删掉;userId 每次运行都是新的,可以反复跑。
 * 运行时控制台会刷 SQL 日志(application-dev.yaml 里开的),嫌吵可以把那里的 log-impl 改成 NoLoggingImpl。
 */
@SpringBootTest
class PostLikeLoadTest {

    @Autowired
    private PostLikeService likeService;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private PostLikeMapper postLikeMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void likeQpsAndP99() throws InterruptedException {
        // ---------- 准备:造一个压测专用帖子,点赞接口要求帖子必须存在 ----------
        Post post = Post.builder().userId(0L).title("压测帖子").content("压测专用").likeCount(0L).build();
        postMapper.insert(post);
        Long postId = post.getId();
        // userId 从一个很大的数开始自增,保证永不重复,不会撞上"请勿重复点赞"
        AtomicLong nextUserId = new AtomicLong(System.currentTimeMillis() * 1_000_000);

        int threadCount = 50;
        int likesPerThread = 200;
        int warmUpPerThread = 20; // 开跑前每人先点 20 次热身,把连接池和 Kafka 生产者跑顺,这部分不计时

        // 三个信号:全部热身完 / 开始正式压测 / 全部跑完
        CountDownLatch allWarmedUp = new CountDownLatch(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch allFinished = new CountDownLatch(threadCount);

        // 每次点赞的耗时(纳秒)和报错,多线程往里写,用同步 List 保证安全
        List<Long> costNanos = Collections.synchronizedList(new ArrayList<>());
        List<String> errors = Collections.synchronizedList(new ArrayList<>());

        // ---------- 发起请求 ----------
        long testCostNanos;
        try (ExecutorService threadPool = Executors.newFixedThreadPool(threadCount)) {
            for (int t = 0; t < threadCount; t++) {
                threadPool.submit(() -> {
                    try {
                        for (int i = 0; i < warmUpPerThread; i++) {
                            likeService.like(nextUserId.incrementAndGet(), postId);
                        }
                        allWarmedUp.countDown();
                        startSignal.await(1, TimeUnit.MINUTES); // 等大家热身完一起开跑,计时才公平

                        for (int i = 0; i < likesPerThread; i++) {
                            long begin = System.nanoTime();
                            try {
                                likeService.like(nextUserId.incrementAndGet(), postId);
                            } catch (Exception e) {
                                errors.add(e.toString());
                            }
                            costNanos.add(System.nanoTime() - begin);
                        }
                    } catch (Exception e) {
                        errors.add(e.toString());
                    } finally {
                        allFinished.countDown();
                    }
                });
            }

            // 等所有线程热身完,按下秒表,喊开始
            assertTrue(allWarmedUp.await(2, TimeUnit.MINUTES), "热身没完成,环境可能有问题");
            long testBegin = System.nanoTime();
            startSignal.countDown();
            assertTrue(allFinished.await(5, TimeUnit.MINUTES), "压测没跑完");
            testCostNanos = System.nanoTime() - testBegin;
        }

        // ---------- 统计 ----------
        long[] costs = new long[costNanos.size()];
        for (int i = 0; i < costs.length; i++) {
            costs[i] = costNanos.get(i);
        }
        Arrays.sort(costs);

        // 一万个请求取 P99,就是排序后取第 9900 个
        int p50Index = (int) Math.ceil(costs.length * 0.50) - 1;
        int p95Index = (int) Math.ceil(costs.length * 0.95) - 1;
        int p99Index = (int) Math.ceil(costs.length * 0.99) - 1;

        double seconds = testCostNanos / 1_000_000_000.0;
        double qps = costs.length / seconds;
        double avgMs = Arrays.stream(costs).average().orElse(0) / 1_000_000.0;

        // ---------- 打印 ----------
        System.out.println();
        System.out.println("================ 点赞压测报告 ================");
        System.out.println("并发模型        : " + threadCount + " 线程 × " + likesPerThread + " 次点赞(热身 " + warmUpPerThread + " 次/线程)");
        System.out.println("成功 / 失败     : " + (costs.length - errors.size()) + " / " + errors.size());
        System.out.printf("压测时长        : %.2f 秒%n", seconds);
        System.out.printf("QPS             : %.1f%n", qps);
        System.out.printf("平均延迟        : %.2f ms%n", avgMs);
        System.out.printf("P50 / P95 / P99 : %.2f / %.2f / %.2f ms%n",
                costs[p50Index] / 1_000_000.0, costs[p95Index] / 1_000_000.0, costs[p99Index] / 1_000_000.0);
        System.out.printf("最大延迟        : %.2f ms%n", costs[costs.length - 1] / 1_000_000.0);
        if (!errors.isEmpty()) {
            System.out.println("首个错误        : " + errors.getFirst());
        }

        // ---------- 验证:点赞只是发了 Kafka 消息,轮询等消费端全部写进 MySQL,一条不丢才算过 ----------
        long expectedLikeCount = (long) threadCount * (likesPerThread + warmUpPerThread);
        long deadline = System.currentTimeMillis() + 60_000;
        while (System.currentTimeMillis() < deadline) {
            if (postMapper.selectById(postId).getLikeCount() == expectedLikeCount) {
                break;
            }
            Thread.sleep(500);
        }
        System.out.printf("最终一致性      : 期望落库 %d 次,实际 %d 次%n",
                expectedLikeCount, postMapper.selectById(postId).getLikeCount());
        assertEquals(expectedLikeCount, postMapper.selectById(postId).getLikeCount(), "落库数对不上,Kafka 链路可能丢事件");
        assertEquals(0, errors.size(), "有请求失败,第一个: " + (errors.isEmpty() ? "" : errors.getFirst()));

        // ---------- 清理:删掉本次压测的帖子、点赞记录、Redis 数据 ----------
        postLikeMapper.delete(new LambdaQueryWrapper<PostLike>().eq(PostLike::getPostId, postId));
        postMapper.deleteById(postId);
        stringRedisTemplate.delete(List.of(
                PostLikeRedisKey.POST_LIKE_USERS + postId,
                PostLikeRedisKey.POST_LIKE_COUNT + postId));
    }
}
