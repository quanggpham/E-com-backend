package com.example.demo.dto.response;

import com.example.demo.enums.DiscountType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderValue;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDate startDate;
    private LocalDate expirationDate;
    private boolean active;
    private LocalDate createdAt;
}