package org.example.simpleweibobackend.user.profile;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.UserVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 查看当前登录用户的个人信息,登录态由 AuthInterceptor 校验
     */
    @GetMapping("/me")
    public Result<UserVO> me() {
        return Result.success(profileService.me());
    }
}
