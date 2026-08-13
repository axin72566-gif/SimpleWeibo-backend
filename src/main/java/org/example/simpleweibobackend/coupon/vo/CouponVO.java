package org.example.simpleweibobackend.coupon.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.coupon.constant.CouponStatus;
import org.example.simpleweibobackend.coupon.entity.Coupon;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "优惠券信息")
public class CouponVO {

    @Schema(description = "优惠券ID")
    private Long id;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "折扣率(1-99)，如80表示8折")
    private Integer discountRate;

    @Schema(description = "发行总量")
    private Integer totalQuantity;

    @Schema(description = "剩余库存")
    private Integer stockRemaining;

    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    @Schema(description = "状态")
    private CouponStatus status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public static CouponVO from(Coupon coupon) {
        return new CouponVO(coupon.getId(), coupon.getName(), coupon.getDiscountRate(),
                coupon.getTotalQuantity(), coupon.getStockRemaining(), coupon.getStartTime(), coupon.getEndTime(),
                coupon.getStatus(), coupon.getCreateTime());
    }
}
