package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductReviewListResponse {
    private ProductReviewStatsResponse stats;
    private PageResponse<ReviewResponse> reviews;
}
