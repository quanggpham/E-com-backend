package com.example.demo.controller;

import com.example.demo.dto.request.BannerReorderRequest;
import com.example.demo.dto.request.BannerRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.BannerResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.BannerService;
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
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BannerResponse>>> getAllBanners(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<BannerResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách banner thành công")
                        .data(bannerService.getAllBanners(pageable))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> getBannerById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.<BannerResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy chi tiết banner thành công")
                        .data(bannerService.getBannerById(id))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(@Valid @RequestBody BannerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<BannerResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Tạo banner thành công")
                        .data(bannerService.createBanner(request))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody BannerRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<BannerResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật banner thành công")
                        .data(bannerService.updateBanner(id, request))
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .message("Xóa banner thành công")
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changeBannerStatus(
            @PathVariable Long id,
            @RequestParam boolean active
    ) {
        bannerService.changeBannerStatus(id, active);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message(active ? "Đã kích hoạt banner" : "Đã vô hiệu hóa banner")
                        .build()
        );
    }

    @PatchMapping("/reorder")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> reorderBanners(
            @Valid @RequestBody List<@Valid BannerReorderRequest> requests
    ) {
        return ResponseEntity.ok(
                ApiResponse.<List<BannerResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật thứ tự banner thành công")
                        .data(bannerService.reorderBanners(requests))
                        .build()
        );
    }
}
