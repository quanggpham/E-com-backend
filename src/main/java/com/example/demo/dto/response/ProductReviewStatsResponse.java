package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Builder
public class ProductReviewStatsResponse {
    private Long productId;
    private BigDecimal avgRating;
    private Long totalReviews;
    private Map<String, Long> ratingDistribution;
}
