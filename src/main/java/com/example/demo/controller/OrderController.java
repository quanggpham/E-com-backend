package com.example.demo.controller;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.security.UserPrincipal;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ServerProperties serverProperties;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CheckoutRequest checkoutRequest
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<OrderResponse>builder()
                        .message("Đặt đơn hàng thành công")
                        .status(HttpStatus.CREATED.value())
                        .data(orderService.createOrder(userPrincipal.getId(), checkoutRequest))
                        .build()
        );
    }
}
