package org.example.simpleweibobackend.vote.queue;

import org.example.simpleweibobackend.vote.event.VoteEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class VoteEventQueue {

    private static final int CAPACITY = 10_000;

    private final BlockingQueue<VoteEvent> queue = new ArrayBlockingQueue<>(CAPACITY);

    public boolean offer(VoteEvent event) {
        return queue.offer(event);
    }

    public List<VoteEvent> drain(int maxElements) {
        List<VoteEvent> events = new ArrayList<>(maxElements);
        queue.drainTo(events, maxElements);
        return events;
    }
}
