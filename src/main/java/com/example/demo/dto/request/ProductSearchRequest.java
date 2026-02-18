package com.example.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductSearchRequest {
    private String name;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Long categoryId;
    private int page = 1; // Giá trị mặc định
    private int size = 10;
    private String sortBy = "id";
    private String sortDirection = "asc";
}
