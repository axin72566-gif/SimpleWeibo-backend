package org.example.simpleweibobackend.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.product.dto.PurchaseProductRequest;
import org.example.simpleweibobackend.product.service.ProductOrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    @PostMapping
    public Result<Long> createOrder(@Valid @RequestBody PurchaseProductRequest request) {
        return Result.success(productOrderService.createOrder(request));
    }

}
