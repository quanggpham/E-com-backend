package com.example.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {

    private String name;

    private String description;

    @Positive(message = "Giá sản phẩm phải lớn hơn 0")
    private BigDecimal price;

    private Long stockQuantity;
    private String thumbnailUrl;

    private Boolean isActive;


    private Long categoryId;
}