package org.example.simpleweibobackend.product.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductOrderStatus {

    PENDING_PAYMENT(0),
    PAID(1),
    CLOSED(2);

    @EnumValue
    private final int value;
}
