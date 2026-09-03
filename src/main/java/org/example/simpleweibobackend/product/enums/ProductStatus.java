package org.example.simpleweibobackend.product.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {

    OFF_SALE(0),
    ON_SALE(1);

    @EnumValue
    private final int value;
}
