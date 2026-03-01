package com.example.demo.controller;

import com.example.demo.dto.request.UpdateOrderStatusRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getAll(
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<PageResponse<OrderResponse>>builder()
                        .data(orderService.getAll(pageable))
                        .message("Xem tất cả đơn hàng thành công")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<OrderResponse>builder()
                        .message("Xem đơn hàng thành công")
                        .status(HttpStatus.OK.value())
                        .data(orderService.adminGetById(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusRequest request) {
        orderService.updateStatus(id, request.getOrderStatus());
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Void>builder()
                        .message("Xem đơn hàng thành công")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }
}
