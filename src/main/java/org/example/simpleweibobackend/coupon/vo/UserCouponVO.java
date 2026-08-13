package org.example.simpleweibobackend.coupon.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "秒杀领券结果")
public class UserCouponVO {

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "优惠券名称")
    private String couponName;

    @Schema(description = "折扣率(1-99)，如80表示8折")
    private Integer discountRate;

    public static UserCouponVO of(Long couponId, Long userId, String couponName, Integer discountRate) {
        return new UserCouponVO(couponId, userId, couponName, discountRate);
    }
}
