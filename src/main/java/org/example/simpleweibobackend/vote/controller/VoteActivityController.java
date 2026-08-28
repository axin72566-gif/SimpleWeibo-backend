package org.example.simpleweibobackend.vote.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.vote.dto.CreateVoteActivityRequest;
import org.example.simpleweibobackend.vote.service.VoteActivityService;
import org.example.simpleweibobackend.vote.vo.VoteActivityVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vote-activities")
@RequiredArgsConstructor
public class VoteActivityController {

    private final VoteActivityService voteActivityService;

    @PostMapping
    public Result<VoteActivityVO> createVoteActivity(@Valid @RequestBody CreateVoteActivityRequest request) {
        return Result.success(voteActivityService.createVoteActivity(request));
    }

    @GetMapping("/{id}")
    public Result<VoteActivityVO> getVoteActivity(@PathVariable Long id) {
        return Result.success(voteActivityService.getVoteActivity(id));
    }
}
