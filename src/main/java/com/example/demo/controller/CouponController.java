package com.example.demo.controller;

import com.example.demo.dto.request.CouponCalculateRequest;
import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CouponCalculationResponse;
import com.example.demo.dto.response.CouponResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.enums.PromotionType;
import com.example.demo.security.UserPrincipal;
import com.example.demo.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@Valid @RequestBody CouponRequest request) {
        CouponResponse data = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CouponResponse>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Tạo mã giảm giá thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CouponResponse>>> getAllCoupons(
            @RequestParam(required = false) PromotionType promotionType,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long productId,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        PageResponse<CouponResponse> data = couponService.getAllCoupons(promotionType, categoryId, productId, pageable);
        return ResponseEntity.ok(
                ApiResponse.<PageResponse<CouponResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy danh sách mã giảm giá thành công")
                        .data(data)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(@PathVariable Long id) {
        CouponResponse data = couponService.getCouponById(id);
        return ResponseEntity.ok(
                ApiResponse.<CouponResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy thông tin mã giảm giá thành công")
                        .data(data)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        CouponResponse data = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(
                ApiResponse.<CouponResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cập nhật mã giảm giá thành công")
                        .data(data)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .message("Xóa mã giảm giá thành công")
                        .build()
        );
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<CouponCalculationResponse>> calculateDiscount(
            @Valid @RequestBody CouponCalculateRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
            ) {
        CouponCalculationResponse discountData = couponService.calculateDiscount(request, userPrincipal.getId());
        return ResponseEntity.ok(
                ApiResponse.<CouponCalculationResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tính toán số tiền giảm giá thành công")
                        .data(discountData)
                        .build()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> changeCouponStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        couponService.changeCouponStatus(id, active);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message(active ? "Đã kích hoạt mã giảm giá" : "Đã vô hiệu hóa mã giảm giá")
                        .build()
        );
    }
}
