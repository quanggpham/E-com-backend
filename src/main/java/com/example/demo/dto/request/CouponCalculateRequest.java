package com.example.demo.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CouponCalculateRequest {

    @NotBlank(message = "Mã giảm giá không được để trống")
    private String code;

    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<CartItemRequest> items;
}
