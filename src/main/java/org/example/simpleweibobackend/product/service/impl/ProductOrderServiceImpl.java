package org.example.simpleweibobackend.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.common.util.UserContext;
import org.example.simpleweibobackend.product.dto.PurchaseProductRequest;
import org.example.simpleweibobackend.product.entity.ProductOrder;
import org.example.simpleweibobackend.product.mapper.ProductOrderMapper;
import org.example.simpleweibobackend.product.service.ProductOrderService;
import org.example.simpleweibobackend.product.entity.Product;
import org.example.simpleweibobackend.product.enums.ProductStatus;
import org.example.simpleweibobackend.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductMapper productMapper;
    private final ProductOrderMapper productOrderMapper;

    @Override
    @Transactional
    public Long purchaseProduct(PurchaseProductRequest request) {
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
        productOrderMapper.insert(order);
        return order.getId();
    }
}
