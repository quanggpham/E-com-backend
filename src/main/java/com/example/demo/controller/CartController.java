package com.example.demo.controller;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.request.UpdateCartRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.service.CartService;
import com.example.demo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addToCart(
//            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam Long userId,
            @RequestBody AddToCartRequest request
            )
    {
        cartService.addToCart(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Thêm vào giỏ hàng thành công!")
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestParam Long userId // tam thoi thoi
    ) {
        return ResponseEntity.
                status(HttpStatus.OK)
                .body(ApiResponse.<CartResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Get giỏ hàng thành công")
                        .data(cartService.getCart(userId))
                        .build());
    }

    @PutMapping("/item")
    public ResponseEntity<ApiResponse<Void>> updateQuantity(
            @RequestParam Long userId,
            @RequestBody UpdateCartRequest request
    ) {
        cartService.updateQuantity(userId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<Void>builder()
                        .message("Cập nhật số lượng thành công")
                        .status(HttpStatus.OK.value())
                        .build());
    }

    @DeleteMapping("/item/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @RequestParam Long userId,
            @PathVariable Long productId

    ) {
        cartService.deleteItem(userId, productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Xóa sản phẩm thành công")
                        .build());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestParam Long userId) {
        cartService.clear(userId);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Giỏ hàng đã được dọn sạch")
                        .build()
        );
    }

}
