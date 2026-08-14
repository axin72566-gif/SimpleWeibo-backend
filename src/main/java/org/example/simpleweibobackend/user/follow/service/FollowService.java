package org.example.simpleweibobackend.user.follow.service;

public interface FollowService {

    void follow(Long followingId);

    void unfollow(Long followingId);
}
