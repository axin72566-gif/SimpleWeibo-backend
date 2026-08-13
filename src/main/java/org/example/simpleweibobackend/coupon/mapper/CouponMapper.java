package org.example.simpleweibobackend.coupon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.coupon.entity.Coupon;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {
}
