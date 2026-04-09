package com.example.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PromotionBannerRequest {

    @NotBlank(message = "Tiêu đề promotion banner không được để trống")
    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @Size(max = 100, message = "Discount label tối đa 100 ký tự")
    private String discountLabel;

    @Size(max = 100, message = "Coupon code tối đa 100 ký tự")
    private String couponCode;

    private String description;

    @Size(max = 500, message = "Link URL tối đa 500 ký tự")
    private String linkUrl;

    @Size(max = 100, message = "Màu nền tối đa 100 ký tự")
    private String bgColor;

    @Min(value = 0, message = "Thứ tự hiển thị phải lớn hơn hoặc bằng 0")
    private Integer displayOrder = 0;

    @JsonProperty("isActive")
    private Boolean isActive = true;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
