package com.example.demo.dto.request;

import com.example.demo.enums.BannerBadgeIcon;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BannerRequest {

    @NotBlank(message = "Tiêu đề banner không được để trống")
    @Size(max = 255, message = "Tiêu đề banner tối đa 255 ký tự")
    private String title;

    @Size(max = 255, message = "Phụ đề tối đa 255 ký tự")
    private String subtitle;

    private String description;

    @NotBlank(message = "Ảnh banner không được để trống")
    @Size(max = 500, message = "URL ảnh tối đa 500 ký tự")
    private String imageUrl;

    @Size(max = 500, message = "URL liên kết tối đa 500 ký tự")
    private String linkUrl;

    @Size(max = 100, message = "Badge text tối đa 100 ký tự")
    private String badgeText;

    private BannerBadgeIcon badgeIcon;

    @Size(max = 100, message = "Overlay color tối đa 100 ký tự")
    private String overlayColor;

    private Integer displayOrder = 0;

    @JsonProperty("isActive")
    private Boolean isActive = true;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
