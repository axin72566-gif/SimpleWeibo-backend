package org.example.simpleweibobackend.product.service;

import org.example.simpleweibobackend.product.dto.PurchaseProductRequest;

public interface ProductOrderService {

    Long createOrder(PurchaseProductRequest request);

    void payOrder(Long orderId);

    void closeExpiredOrders();
}
