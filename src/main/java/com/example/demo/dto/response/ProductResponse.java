package com.example.demo.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long stockQuantity;
    private String thumbnailUrl;
    private Boolean isActive;
    private CategoryResponse category;
    private LocalDateTime createdAt;
}