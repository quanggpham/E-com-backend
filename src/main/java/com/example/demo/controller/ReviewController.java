package com.example.demo.controller;

import com.example.demo.dto.request.CreateReviewRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.security.UserPrincipal;
import com.example.demo.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ReviewResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Tao review thanh cong")
                        .data(reviewService.createReview(currentUser.getId(), request))
                        .build()
        );
    }
}
