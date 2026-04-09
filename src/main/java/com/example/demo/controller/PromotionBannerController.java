package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PromotionBannerResponse;
import com.example.demo.service.PromotionBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/promotion-banners")
@RequiredArgsConstructor
public class PromotionBannerController {

    private final PromotionBannerService promotionBannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionBannerResponse>>> getActivePromotionBanners() {
        return ResponseEntity.ok(
                ApiResponse.<List<PromotionBannerResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách promotion banner thành công")
                        .data(promotionBannerService.getActivePromotionBanners())
                        .build()
        );
    }
}
