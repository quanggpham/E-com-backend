package com.example.demo.controller;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.response.ApiResponse;
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
    private final CategoryService categoryService;

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
}
