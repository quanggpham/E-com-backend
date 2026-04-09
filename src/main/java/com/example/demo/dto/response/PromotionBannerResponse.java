package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PromotionBannerResponse {
    private Long id;
    private String title;
    private String discountLabel;
    private String couponCode;
    private String description;
    private String linkUrl;
    private String bgColor;
    private Integer displayOrder;

    @JsonProperty("isActive")
    private boolean active;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
}
