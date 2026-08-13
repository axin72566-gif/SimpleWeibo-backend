package org.example.simpleweibobackend.coupon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.simpleweibobackend.coupon.entity.UserCoupon;

@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    @Insert("INSERT IGNORE INTO user_coupon (user_id, coupon_id, status) VALUES (#{userId}, #{couponId}, 'UNUSED')")
    int insertIgnore(UserCoupon userCoupon);
}
