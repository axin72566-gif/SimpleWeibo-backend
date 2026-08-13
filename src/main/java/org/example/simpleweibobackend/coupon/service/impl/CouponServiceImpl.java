package org.example.simpleweibobackend.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.CouponStatus;
import org.example.simpleweibobackend.coupon.dto.CreateCouponRequest;
import org.example.simpleweibobackend.coupon.entity.Coupon;
import org.example.simpleweibobackend.coupon.mapper.CouponMapper;
import org.example.simpleweibobackend.coupon.service.CouponService;
import org.example.simpleweibobackend.coupon.vo.CouponVO;
import org.example.simpleweibobackend.exception.BizException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final StringRedisTemplate redisTemplate;

    @Override
    public CouponVO create(CreateCouponRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "生效结束时间必须晚于开始时间");
        }

        Coupon coupon = new Coupon();
        coupon.setName(request.getName());
        coupon.setDiscountRate(request.getDiscountRate());
        coupon.setTotalQuantity(request.getTotalQuantity());
        coupon.setStartTime(request.getStartTime());
        coupon.setEndTime(request.getEndTime());
        coupon.setStatus(CouponStatus.DRAFT);
        couponMapper.insert(coupon);

        return CouponVO.from(coupon);
    }

    @Override
    public void publish(Long id) {
        Coupon coupon = getCouponOrThrow(id);
        if (coupon.getStatus() != CouponStatus.DRAFT) {
            throw new BizException(ErrorCode.BAD_REQUEST, "仅草稿状态的优惠券可发布");
        }
        // 更新状态并初始化库存
        Coupon update = new Coupon();
        update.setId(id);
        update.setStatus(CouponStatus.PUBLISHED);
        update.setStockRemaining(coupon.getTotalQuantity());
        couponMapper.updateById(update);
        // 预热 Redis 库存
        redisTemplate.opsForValue().set("coupon:stock:" + id, String.valueOf(coupon.getTotalQuantity()));
    }

    @Override
    public void offline(Long id) {
        Coupon coupon = getCouponOrThrow(id);
        if (coupon.getStatus() != CouponStatus.PUBLISHED) {
            throw new BizException(ErrorCode.BAD_REQUEST, "仅已发布状态的优惠券可下架");
        }
        updateStatus(id, CouponStatus.OFFLINE);
    }

    @Override
    public PageVO<CouponVO> list(int page, int size) {
        Page<Coupon> couponPage = couponMapper.selectPage(
                new Page<>(page, size),
                new QueryWrapper<Coupon>().orderByDesc("create_time"));
        List<CouponVO> records = couponPage.getRecords().stream()
                .map(CouponVO::from)
                .toList();
        return PageVO.of(records, couponPage.getTotal(), page, size);
    }

    @Override
    public CouponVO detail(Long id) {
        return CouponVO.from(getCouponOrThrow(id));
    }

    @Override
    public void delete(Long id) {
        Coupon coupon = getCouponOrThrow(id);
        if (coupon.getStatus() != CouponStatus.DRAFT) {
            throw new BizException(ErrorCode.BAD_REQUEST, "仅草稿状态的优惠券可删除");
        }
        couponMapper.deleteById(id);
    }

    private Coupon getCouponOrThrow(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "优惠券不存在");
        }
        return coupon;
    }

    private void updateStatus(Long id, CouponStatus status) {
        Coupon update = new Coupon();
        update.setId(id);
        update.setStatus(status);
        couponMapper.updateById(update);
    }
}
