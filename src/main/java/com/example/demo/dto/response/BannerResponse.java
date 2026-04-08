package com.example.demo.dto.response;

import com.example.demo.enums.BannerBadgeIcon;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BannerResponse {
    private Long id;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private String linkUrl;
    private String badgeText;
    private BannerBadgeIcon badgeIcon;
    private String overlayColor;
    private Integer displayOrder;

    @JsonProperty("isActive")
    private boolean active;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
