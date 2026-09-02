package org.example.simpleweibobackend.vote.queue;

import org.example.simpleweibobackend.vote.event.VoteEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VoteEventQueueTest {

    @Test
    void drainsEventsInOrderUpToLimit() {
        VoteEventQueue queue = new VoteEventQueue();
        VoteEvent first = new VoteEvent(10L, 1L, 20L);
        VoteEvent second = new VoteEvent(10L, 2L, 21L);
        VoteEvent third = new VoteEvent(10L, 3L, 22L);
        queue.offer(first);
        queue.offer(second);
        queue.offer(third);

        List<VoteEvent> firstBatch = queue.drain(2);
        List<VoteEvent> secondBatch = queue.drain(2);

        assertThat(firstBatch).containsExactly(first, second);
        assertThat(secondBatch).containsExactly(third);
        assertThat(queue.drain(2)).isEmpty();
    }
}
