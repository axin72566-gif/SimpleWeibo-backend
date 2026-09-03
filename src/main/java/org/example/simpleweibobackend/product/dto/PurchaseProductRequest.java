package org.example.simpleweibobackend.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PurchaseProductRequest {

    @NotNull(message = "商品ID不能为空")
    @Positive(message = "商品ID必须是正整数")
    private Long productId;

    @NotNull(message = "购买数量不能为空")
    @Positive(message = "购买数量必须是正整数")
    private Integer quantity;
}
