package org.example.simpleweibobackend.coupon.service;

import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.coupon.dto.CreateCouponRequest;
import org.example.simpleweibobackend.coupon.vo.CouponVO;

public interface CouponService {

    CouponVO create(CreateCouponRequest request);

    void publish(Long id);

    void offline(Long id);

    PageVO<CouponVO> list(int page, int size);

    CouponVO detail(Long id);

    void delete(Long id);
}
