package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long itemId;
    private Long productId;
    private String productName;
    private String thumbnailUrl;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;
}
