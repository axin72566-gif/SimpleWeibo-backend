package org.example.simpleweibobackend.vote.queue.consumer;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.vote.entity.VoteRecord;
import org.example.simpleweibobackend.vote.event.VoteEvent;
import org.example.simpleweibobackend.vote.mapper.VoteRecordMapper;
import org.example.simpleweibobackend.vote.mapper.VoteStatMapper;
import org.example.simpleweibobackend.vote.queue.VoteEventQueue;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VoteEventQueueConsumer {

    private static final int BATCH_SIZE = 500;
    private static final String DIRTY_USER_KEY = "vote:activity:dirty:user:{%d}";

    private final VoteEventQueue voteEventQueue;
    private final VoteRecordMapper voteRecordMapper;
    private final VoteStatMapper voteStatMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final TransactionTemplate transactionTemplate;

    // 上一批消费结束后等待 1 秒，再处理下一批。
    @Scheduled(fixedDelay = 1000)
    public void consume() {
        // 从内存队列移出最多 500 条事件，集中落库。
        List<VoteEvent> events = voteEventQueue.drain(BATCH_SIZE);
        if (events.isEmpty()) {
            return;
        }

        Map<Long, List<VoteEvent>> eventsByActivity = groupByActivity(events);
        for (Map.Entry<Long, List<VoteEvent>> entry : eventsByActivity.entrySet()) {
            consumeActivity(entry.getKey(), entry.getValue());
        }
    }

    // Redis 数据按活动存储，先分组，后续每个活动独立处理。
    private Map<Long, List<VoteEvent>> groupByActivity(List<VoteEvent> events) {
        Map<Long, List<VoteEvent>> result = new LinkedHashMap<>();
        for (VoteEvent event : events) {
            List<VoteEvent> activityEvents = result.computeIfAbsent(event.getActivityId(), k -> new ArrayList<>());
            activityEvents.add(event);
        }
        return result;
    }

    private void consumeActivity(Long activityId, List<VoteEvent> events) {
        // 只统计当前批次的票数增量，不读取仍在实时增长的 Redis 总票数。
        Map<Long, Long> increments = new LinkedHashMap<>();
        List<VoteRecord> records = new ArrayList<>(events.size());
        for (VoteEvent event : events) {
            increments.put(event.getPostId(), increments.getOrDefault(event.getPostId(), 0L) + 1);

            VoteRecord record = new VoteRecord();
            record.setActivityId(event.getActivityId());
            record.setUserId(event.getUserId());
            record.setPostId(event.getPostId());
            records.add(record);
        }

        // 同一活动的投票明细和票数增量在一个事务中落库。
        transactionTemplate.executeWithoutResult(status -> {
            voteRecordMapper.insert(records);
            for (Map.Entry<Long, Long> increment : increments.entrySet()) {
                voteStatMapper.incrementCount(activityId, increment.getKey(), increment.getValue());
            }
        });

        acknowledge(activityId, events);
    }

    // 事务提交成功后才清理 dirty 用户；落库失败时保留标记供后续补偿。
    private void acknowledge(Long activityId, List<VoteEvent> events) {
        Object[] userIds = new Object[events.size()];
        for (int i = 0; i < events.size(); i++) {
            userIds[i] = events.get(i).getUserId().toString();
        }
        stringRedisTemplate.opsForSet().remove(
                DIRTY_USER_KEY.formatted(activityId), userIds);
    }
}
