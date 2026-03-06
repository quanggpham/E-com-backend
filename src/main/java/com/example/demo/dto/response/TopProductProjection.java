package com.example.demo.dto.response;

public interface TopProductProjection {
    Long getProductId();
    String getProductName();
    String getProductImage();
    Long getTotalQuantity();
}