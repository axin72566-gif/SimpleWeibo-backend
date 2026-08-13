package org.example.simpleweibobackend.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;
import org.example.simpleweibobackend.coupon.constant.CouponStatus;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("coupon")
public class Coupon extends BaseEntity {

    private String name;

    private Integer discountRate;

    private Integer totalQuantity;

    private Integer stockRemaining;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private CouponStatus status;
}
