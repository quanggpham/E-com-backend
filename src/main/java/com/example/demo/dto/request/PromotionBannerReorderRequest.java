package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionBannerReorderRequest {

    @NotNull(message = "Id promotion banner không được để trống")
    private Long id;

    @NotNull(message = "Thứ tự hiển thị không được để trống")
    @Min(value = 0, message = "Thứ tự hiển thị phải lớn hơn hoặc bằng 0")
    private Integer displayOrder;
}
