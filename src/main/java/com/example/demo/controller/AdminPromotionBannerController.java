package com.example.demo.controller;

import com.example.demo.dto.request.PromotionBannerReorderRequest;
import com.example.demo.dto.request.PromotionBannerRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.PromotionBannerResponse;
import com.example.demo.service.PromotionBannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/promotion-banners")
@RequiredArgsConstructor
public class AdminPromotionBannerController {

    private final PromotionBannerService promotionBannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PromotionBannerResponse>>> getAllPromotionBanners(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<PromotionBannerResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách promotion banner thành công")
                        .data(promotionBannerService.getAllPromotionBanners(pageable))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionBannerResponse>> getPromotionBannerById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<PromotionBannerResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy chi tiết promotion banner thành công")
                        .data(promotionBannerService.getPromotionBannerById(id))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromotionBannerResponse>> createPromotionBanner(
            @Valid @RequestBody PromotionBannerRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<PromotionBannerResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Tạo promotion banner thành công")
                        .data(promotionBannerService.createPromotionBanner(request))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionBannerResponse>> updatePromotionBanner(
            @PathVariable Long id,
            @Valid @RequestBody PromotionBannerRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PromotionBannerResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật promotion banner thành công")
                        .data(promotionBannerService.updatePromotionBanner(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePromotionBanner(@PathVariable Long id) {
        promotionBannerService.deletePromotionBanner(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .message("Xóa promotion banner thành công")
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changePromotionBannerStatus(
            @PathVariable Long id,
            @RequestParam boolean active
    ) {
        promotionBannerService.changePromotionBannerStatus(id, active);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message(active ? "Đã kích hoạt promotion banner" : "Đã vô hiệu hóa promotion banner")
                        .build()
        );
    }

    @PatchMapping("/reorder")
    public ResponseEntity<ApiResponse<List<PromotionBannerResponse>>> reorderPromotionBanners(
            @Valid @RequestBody List<@Valid PromotionBannerReorderRequest> requests
    ) {
        return ResponseEntity.ok(
                ApiResponse.<List<PromotionBannerResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thứ tự promotion banner thành công")
                        .data(promotionBannerService.reorderPromotionBanners(requests))
                        .build()
        );
    }
}
