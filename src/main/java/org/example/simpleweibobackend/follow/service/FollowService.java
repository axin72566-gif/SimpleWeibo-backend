package org.example.simpleweibobackend.follow.service;

public interface FollowService {

    void follow(Long followingId);

    void unfollow(Long followingId);
}
