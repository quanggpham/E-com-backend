package com.example.demo.controller;

import com.example.demo.dto.request.SellerReplyRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller/reviews")
@RequiredArgsConstructor
public class SellerReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<ReviewResponse>> replyToReview(
            @PathVariable Long id,
            @Valid @RequestBody SellerReplyRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.<ReviewResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tra loi review thanh cong")
                        .data(reviewService.replyToReview(id, request))
                        .build()
        );
    }
}
