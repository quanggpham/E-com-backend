package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ProductReviewListResponse;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<ProductReviewListResponse>> getProductReviews(
            @PathVariable Long id,
            @RequestParam(required = false) Integer rating,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.<ProductReviewListResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lay danh sach review thanh cong")
                .data(reviewService.getProductReviews(id, rating, pageable))
                .build());
    }
}
