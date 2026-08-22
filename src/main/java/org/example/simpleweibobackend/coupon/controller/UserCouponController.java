package org.example.simpleweibobackend.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.coupon.service.UserCouponService;
import org.example.simpleweibobackend.coupon.vo.UserCouponVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户优惠券")
@RestController
@RequestMapping("/api/user-coupons")
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponService userCouponService;

    @Operation(summary = "秒杀领取优惠券")
    @PostMapping("/seckill/{couponId}")
    public Result<UserCouponVO> seckill(@Parameter(description = "优惠券ID") @PathVariable Long couponId) {
        return Result.success(userCouponService.seckill(couponId));
    }
}
