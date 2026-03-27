package com.example.demo.controller;

import com.example.demo.dto.request.AdminReviewStatusRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.enums.ReviewStatus;
import com.example.demo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getAdminReviews(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) ReviewStatus status,
            @RequestParam(required = false) Integer minReportCount,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<ReviewResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lay danh sach review can duyet thanh cong")
                        .data(reviewService.getAdminReviews(productId, userId, status, minReportCount, pageable))
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminReviewStatusRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<ReviewResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cap nhat trang thai review thanh cong")
                        .data(reviewService.updateReviewStatus(id, request))
                        .build()
        );
    }
}
