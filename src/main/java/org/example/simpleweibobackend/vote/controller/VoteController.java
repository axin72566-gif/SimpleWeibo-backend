package org.example.simpleweibobackend.vote.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.vote.dto.CastVoteRequest;
import org.example.simpleweibobackend.vote.service.VoteService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public Result<Void> castVote(@Valid @RequestBody CastVoteRequest request) {
        voteService.castVote(request);
        return Result.success();
    }
}
