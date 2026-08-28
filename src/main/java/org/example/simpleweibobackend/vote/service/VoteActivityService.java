package org.example.simpleweibobackend.vote.service;

import org.example.simpleweibobackend.vote.dto.CreateVoteActivityRequest;
import org.example.simpleweibobackend.vote.vo.VoteActivityVO;

public interface VoteActivityService {

    VoteActivityVO createVoteActivity(CreateVoteActivityRequest request);

    VoteActivityVO getVoteActivity(Long id);
}
