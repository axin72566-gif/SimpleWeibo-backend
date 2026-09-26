package org.example.simpleweibobackend.post.create.audit;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * 发帖链路 P99 延迟测试:启动完整应用(随机端口),发真实 HTTP 请求,
 * 完整走 Controller -> 审核责任链 -> MySQL 落库,统计接口耗时分位数。
 * <p>
 * 运行方式: mvn test -Dtest=PostAuditP99LatencyTest,需要本地 MySQL(localhost:3305)可用。
 * 测试帖使用固定 userId,每次运行前先清掉上一次的测试帖,可重复执行。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostAuditP99LatencyTest {

    private static final long TEST_USER_ID = 999901L;
    private static final int REQUEST_COUNT = 30;
    private static final String REQUEST_BODY =
            "{\"title\":\"P99延迟测试\",\"content\":\"今天天气不错,出去走走\"}";

    @LocalServerPort
    private int port;

    @Autowired
    private PostMapper postMapper;

    @Test
    void measureCreatePostP99() {
        postMapper.delete(Wrappers.<Post>lambdaQuery().eq(Post::getUserId, TEST_USER_ID));

        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("X-User-Id", String.valueOf(TEST_USER_ID))
                .build();

        long[] costs = new long[REQUEST_COUNT];
        int failed = 0;
        for (int i = 0; i < REQUEST_COUNT; i++) {
            long start = System.nanoTime();
            try {
                int status = client.post()
                        .uri("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(REQUEST_BODY)
                        .retrieve()
                        .toEntity(String.class)
                        .getStatusCode().value();
                if (status != 200) {
                    failed++;
                }
            } catch (Exception e) {
                failed++;
            }
            costs[i] = (System.nanoTime() - start) / 1_000_000;
        }

        if (failed > 0) {
            fail(failed + " 个请求失败,分位数无效");
        }

        long[] sorted = costs.clone();
        Arrays.sort(sorted);
        System.out.printf("请求次数=%d 平均=%dms P50=%dms P95=%dms P99=%dms 最大=%dms%n",
                REQUEST_COUNT,
                Arrays.stream(sorted).sum() / REQUEST_COUNT,
                percentile(sorted, 50),
                percentile(sorted, 95),
                percentile(sorted, 99),
                sorted[REQUEST_COUNT - 1]);
    }

    /**
     * p 取 0~100,返回排序后数组中对应分位的值(毫秒)
     */
    private static long percentile(long[] sortedCosts, int p) {
        int index = (int) Math.ceil(p / 100.0 * sortedCosts.length) - 1;
        return sortedCosts[Math.max(index, 0)];
    }
}
