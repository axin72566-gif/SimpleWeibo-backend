package org.example.simpleweibobackend.user.service;

public interface FollowService {

    void follow(Long followingId);

    void unfollow(Long followingId);
}
