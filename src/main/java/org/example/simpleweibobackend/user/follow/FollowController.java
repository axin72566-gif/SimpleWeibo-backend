package org.example.simpleweibobackend.user.follow;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.UserVO;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /** 关注 */
    @PostMapping("/follows/{followedUserId}")
    public Result<Void> follow(@PathVariable Long followedUserId) {
        followService.follow(UserContext.getUserId(), followedUserId);
        return Result.success(null);
    }

    /** 取消关注 */
    @DeleteMapping("/follows/{followedUserId}")
    public Result<Void> unfollow(@PathVariable Long followedUserId) {
        followService.unfollow(UserContext.getUserId(), followedUserId);
        return Result.success(null);
    }

    /** 分页查看我的关注列表,按关注时间倒序 */
    @GetMapping("/follows")
    public Result<PageVO<UserVO>> listFollowings(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return Result.success(followService.listFollowings(UserContext.getUserId(), page, size));
    }
}
