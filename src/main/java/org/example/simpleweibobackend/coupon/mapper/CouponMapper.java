package org.example.simpleweibobackend.coupon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.simpleweibobackend.coupon.entity.Coupon;

@Mapper
public interface CouponMapper extends BaseMapper<Coupon> {

    @Update("UPDATE coupon SET stock_remaining = stock_remaining - 1 WHERE id = #{id} AND stock_remaining > 0")
    int decrementStock(@Param("id") Long id);
}
