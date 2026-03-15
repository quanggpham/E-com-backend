package com.example.demo.controller;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.User;
import com.example.demo.security.UserPrincipal;
import com.example.demo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<OrderResponse>builder()
                        .message("Xem chi tiết đơn hàng thành công")
                        .data(orderService.getById(id, userPrincipal.getId()))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        orderService.cancelOrder(id, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Void>builder()
                        .message("Hủy đơn hàng thành công")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByUserId(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<PageResponse<OrderResponse>>builder()
                        .message("Xem danh sách đơn hàng thành công")
                        .status(HttpStatus.OK.value())
                        .data(orderService.getAllByUserId(userPrincipal.getId(), pageable))
                        .build()
        );
    }


}
