package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.ReviewableOrderResponse;
import com.example.demo.security.UserPrincipal;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserReviewController {

    private final ReviewService reviewService;

    @GetMapping("/reviewable-orders")
    public ResponseEntity<ApiResponse<List<ReviewableOrderResponse>>> getReviewableOrders(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(
                ApiResponse.<List<ReviewableOrderResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lay danh sach order co the review thanh cong")
                        .data(reviewService.getReviewableOrders(currentUser.getId()))
                        .build()
        );
    }
}
