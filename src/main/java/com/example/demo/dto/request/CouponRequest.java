package com.example.demo.dto.request;

import com.example.demo.enums.DiscountType;
import com.example.demo.enums.PromotionType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CouponRequest {
    @NotBlank(message = "Mã giảm giá không được để trống")
    private String code;

    @NotNull(message = "Loại giảm giá không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Min(value = 0, message = "Giá trị giảm phải lớn hơn hoặc bằng 0")
    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount;

    @Min(value = 0, message = "Đơn tối thiểu phải lớn hơn hoặc bằng 0")
    private BigDecimal minOrderValue;

    @NotNull(message = "Giới hạn sử dụng không được để trống")
    @Min(value = 1, message = "Giới hạn sử dụng phải ít nhất là 1")
    private Integer usageLimit;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @Future(message = "Ngày kết thúc phải ở tương lai")
    private LocalDate expirationDate;

    @NotNull(message = "Loại khuyến mãi không được để trống")
    private PromotionType promotionType;

    private Long categoryId;

    private Long productId;

    private boolean active = true;
}
