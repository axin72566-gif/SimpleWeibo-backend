package org.example.simpleweibobackend.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponSeckillEvent implements Serializable {

    private Long couponId;

    private Long userId;
}
