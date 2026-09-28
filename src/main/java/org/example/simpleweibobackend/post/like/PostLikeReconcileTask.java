package org.example.simpleweibobackend.post.like;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 点赞对账任务:每 5 分钟一轮全量对账,以 post_like 明细为最终权威,自上而下修复两份副本,
 * 收敛 Redis 丢数据 / 回滚失败等事故留下的计数漂移。
 * 修复原则:MySQL 与 Redis 都偏低才修(偏低说明副本丢了数据);Redis 偏高只告警不修——
 * 分不清是消费在途的正常延迟还是真漂移,强行改小会把在途点赞抹掉反而制造漂移。
 * 修复动作全部按事实写或增量写,幂等,中断后下轮重跑无副作用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeReconcileTask {

    /** Redis 计数缺失或小于明细数时才覆盖,返回 1 表示已校准 */
    private static final RedisScript<Long> CALIBRATE_SCRIPT = new DefaultRedisScript<>("""
            local current = redis.call('GET', KEYS[1])
            if (not current) or (tonumber(current) < tonumber(ARGV[1])) then
                redis.call('SET', KEYS[1], ARGV[1])
                return 1
            end
            return 0
            """, Long.class);

    /** 单条 SADD 命令最多带多少个成员,防止一次塞几万个参数 */
    private static final int SADD_BATCH = 1000;

    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /** fixedDelay:上一轮跑完才计时,不叠加 */
    @Scheduled(initialDelay = 30_000, fixedDelay = 300_000)
    public void reconcile() {
        // 1. 全量扫关系表,按帖分组:一次扫描同时得到每帖的点赞数和点赞人名单
        List<PostLike> likes = postLikeMapper.selectList(null);
        Map<Long, List<PostLike>> likesByPost = likes.stream()
                .collect(Collectors.groupingBy(PostLike::getPostId));

        // 2. 全量拉帖子,逐帖对账
        List<Post> posts = postMapper.selectList(null);
        int calibrated = 0;
        for (Post post : posts) {
            Long postId = post.getId();
            List<PostLike> postLikes = likesByPost.getOrDefault(postId, List.of());
            long detail = postLikes.size();

            // 3. 修 MySQL:like_count 以明细为准做增量修正(与消费端 like_count + n 同款写法,
            //    修正期间并发落库的新计数不会被覆盖丢失)
            long drift = detail - post.getLikeCount();
            if (drift != 0) {
                postMapper.update(null, Wrappers.<Post>update()
                        .setSql("like_count = like_count " + (drift > 0 ? "+ " : "- ") + Math.abs(drift))
                        .eq("id", postId));
                log.warn("MySQL 冗余计数漂移已修复: postId={}, likeCount={}, 明细={}",
                        postId, post.getLikeCount(), detail);
            }

            // 4. 修 Redis:计数缺失或偏低才覆盖为明细数(说明 Redis 丢过数据);偏高只告警
            String countKey = PostLikeRedisKey.POST_LIKE_COUNT + postId;
            Long fixed = stringRedisTemplate.execute(CALIBRATE_SCRIPT, List.of(countKey), String.valueOf(detail));
            if (fixed != null && fixed == 1L) {
                log.warn("Redis 计数漂移已校准: postId={}, 校准为 {}", postId, detail);
                // 5. Redis 丢数据时判重名单和计数一起丢,不回填会让老用户重复点赞反复虚高;SADD 幂等
                String usersKey = PostLikeRedisKey.POST_LIKE_USERS + postId;
                for (int i = 0; i < postLikes.size(); i += SADD_BATCH) {
                    stringRedisTemplate.opsForSet().add(usersKey, postLikes
                            .subList(i, Math.min(i + SADD_BATCH, postLikes.size())).stream()
                            .map(l -> String.valueOf(l.getUserId())).toArray(String[]::new));
                }
                calibrated++;
            } else {
                String current = stringRedisTemplate.opsForValue().get(countKey);
                if (current != null && Long.parseLong(current) > detail) {
                    log.warn("Redis 计数偏高暂不处理(多为消费在途,持续偏高请检查回滚失败残留): postId={}, redis={}, 明细={}",
                            postId, current, detail);
                }
            }
        }
        if (calibrated > 0) {
            log.info("点赞对账完成,本轮校准 {} 帖", calibrated);
        }
    }
}
