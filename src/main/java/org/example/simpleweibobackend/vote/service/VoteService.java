package org.example.simpleweibobackend.vote.service;

import org.example.simpleweibobackend.vote.dto.CastVoteRequest;

public interface VoteService {

    void castVote(CastVoteRequest request);
}
