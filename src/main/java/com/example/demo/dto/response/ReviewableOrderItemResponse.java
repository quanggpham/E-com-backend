package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewableOrderItemResponse {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
}
