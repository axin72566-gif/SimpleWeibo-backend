package org.example.simpleweibobackend.post.queue;

import org.example.simpleweibobackend.post.event.PostLikeEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Component
public class PostLikeEventQueue {

    private static final int CAPACITY = 10_000;

    private final BlockingQueue<PostLikeEvent> queue = new ArrayBlockingQueue<>(CAPACITY);

    public boolean offer(PostLikeEvent event) {
        return queue.offer(event);
    }
}
