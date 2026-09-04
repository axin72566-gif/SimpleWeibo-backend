package org.example.simpleweibobackend.product.task;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.product.service.ProductOrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductOrderTimeoutTask {

    private final ProductOrderService productOrderService;

    @Scheduled(initialDelay = 10_000, fixedDelay = 10_000)
    public void closeExpiredOrders() {
        productOrderService.closeExpiredOrders();
    }
}
