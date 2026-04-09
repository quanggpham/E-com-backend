package com.example.demo.dto.response;

import com.example.demo.enums.PromotionType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class CouponCalculationResponse {
    private BigDecimal discountAmount;
    private List<Long> appliedToItems;
    private PromotionType promotionType;
}
