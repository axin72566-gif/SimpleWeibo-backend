package org.example.simpleweibobackend.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.user.annotation.RequireRole;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.constant.Role;
import org.example.simpleweibobackend.coupon.dto.CreateCouponRequest;
import org.example.simpleweibobackend.coupon.service.CouponService;
import org.example.simpleweibobackend.coupon.vo.CouponVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequireRole(Role.ADMIN)
@Tag(name = "优惠券管理")
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "创建优惠券")
    @PostMapping
    public Result<CouponVO> create(@Valid @RequestBody CreateCouponRequest request) {
        return Result.success(couponService.create(request));
    }

    @Operation(summary = "发布优惠券")
    @PutMapping("/{id}/publish")
    public Result<Void> publish(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        couponService.publish(id);
        return Result.success();
    }

    @Operation(summary = "下架优惠券")
    @PutMapping("/{id}/offline")
    public Result<Void> offline(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        couponService.offline(id);
        return Result.success();
    }

    @Operation(summary = "分页查询优惠券列表")
    @GetMapping
    public Result<PageVO<CouponVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        return Result.success(couponService.list(page, size));
    }

    @Operation(summary = "查询优惠券详情")
    @GetMapping("/{id}")
    public Result<CouponVO> detail(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        return Result.success(couponService.detail(id));
    }

    @Operation(summary = "删除优惠券（仅草稿状态可删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "优惠券ID") @PathVariable Long id) {
        couponService.delete(id);
        return Result.success();
    }
}
