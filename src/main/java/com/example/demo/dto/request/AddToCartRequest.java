package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    private Long productId;
    @Min(1)
    private int quantity;
}
