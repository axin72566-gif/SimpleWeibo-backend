package org.example.simpleweibobackend.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.common.util.UserContext;
import org.example.simpleweibobackend.product.dto.PurchaseProductRequest;
import org.example.simpleweibobackend.product.entity.ProductOrder;
import org.example.simpleweibobackend.product.enums.ProductOrderStatus;
import org.example.simpleweibobackend.product.mapper.ProductOrderMapper;
import org.example.simpleweibobackend.product.service.ProductOrderService;
import org.example.simpleweibobackend.product.entity.Product;
import org.example.simpleweibobackend.product.enums.ProductStatus;
import org.example.simpleweibobackend.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private static final long PAYMENT_TIMEOUT_MINUTES = 15;

    private final ProductMapper productMapper;
    private final ProductOrderMapper productOrderMapper;

    @Override
    @Transactional
    public Long createOrder(PurchaseProductRequest request) {
        Long userId = UserContext.getUserId();
        Product product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new BizException(ErrorCode.BAD_REQUEST, "商品已下架");
        }

        // 扣减库存
        int affectedRows = productMapper.deductStock(
                product.getId(), request.getQuantity(), ProductStatus.ON_SALE.getValue());
        if (affectedRows == 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "商品库存不足或已下架");
        }

        ProductOrder order = new ProductOrder();
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setUnitPrice(product.getPrice());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setStatus(ProductOrderStatus.PENDING_PAYMENT);
        order.setExpireTime(LocalDateTime.now().plusMinutes(PAYMENT_TIMEOUT_MINUTES));
        productOrderMapper.insert(order);
        return order.getId();
    }

    @Override
    @Transactional
    public void payOrder(Long orderId) {
        Long userId = UserContext.getUserId();
        ProductOrder order = productOrderMapper.selectById(orderId);
        if (order == null || !Objects.equals(order.getUserId(), userId)) {
            throw new BizException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        if (order.getStatus() == ProductOrderStatus.PAID) {
            return;
        }
        if (order.getStatus() != ProductOrderStatus.PENDING_PAYMENT) {
            throw new BizException(ErrorCode.BAD_REQUEST, "订单状态不允许支付");
        }

        LocalDateTime now = LocalDateTime.now();
        if (!now.isBefore(order.getExpireTime())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "订单已超时，无法支付");
        }

        int affectedRows = productOrderMapper.payPendingOrder(
                orderId,
                userId,
                ProductOrderStatus.PENDING_PAYMENT.getValue(),
                ProductOrderStatus.PAID.getValue(),
                now);
        if (affectedRows == 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "订单状态已变化，支付失败");
        }
    }

    @Override
    @Transactional
    public void closeExpiredOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<ProductOrder> expiredOrders = productOrderMapper.selectList(
                new LambdaQueryWrapper<ProductOrder>()
                        .eq(ProductOrder::getStatus, ProductOrderStatus.PENDING_PAYMENT)
                        .le(ProductOrder::getExpireTime, now));

        for (ProductOrder order : expiredOrders) {
            int affectedRows = productOrderMapper.closeExpiredOrder(
                    order.getId(),
                    ProductOrderStatus.PENDING_PAYMENT.getValue(),
                    ProductOrderStatus.CLOSED.getValue(),
                    now);
            if (affectedRows == 1) {
                productMapper.restoreStock(order.getProductId(), order.getQuantity());
            }
        }
    }
}
