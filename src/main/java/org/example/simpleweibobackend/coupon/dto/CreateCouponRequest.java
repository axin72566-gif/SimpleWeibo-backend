package org.example.simpleweibobackend.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "创建优惠券请求")
public class CreateCouponRequest {

    @Schema(description = "优惠券名称", example = "夏季促销8折券")
    @NotBlank(message = "优惠券名称不能为空")
    @Size(max = 100, message = "优惠券名称不能超过100个字符")
    private String name;

    @Schema(description = "折扣率(1-99)，如80表示8折", example = "80")
    @NotNull(message = "折扣率不能为空")
    @Min(value = 1, message = "折扣率最小为1")
    @Max(value = 99, message = "折扣率最大为99")
    private Integer discountRate;

    @Schema(description = "发行总量", example = "1000")
    @NotNull(message = "发行总量不能为空")
    @Min(value = 1, message = "发行总量至少为1")
    private Integer totalQuantity;

    @Schema(description = "生效开始时间")
    @NotNull(message = "生效开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "生效结束时间")
    @NotNull(message = "生效结束时间不能为空")
    private LocalDateTime endTime;
}
