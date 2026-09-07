package org.example.simpleweibobackend.post.like.event;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class PostLikeEventQueue {

    private static final int CAPACITY = 10_000;

    private final BlockingQueue<PostLikeEvent> queue = new ArrayBlockingQueue<>(CAPACITY);

    public boolean offer(PostLikeEvent event) {
        return queue.offer(event);
    }

    public List<PostLikeEvent> drain(int maxElements) {
        List<PostLikeEvent> events = new ArrayList<>(maxElements);
        queue.drainTo(events, maxElements);
        return events;
    }
}
