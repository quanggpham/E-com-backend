package com.example.demo.controller;

import com.example.demo.dto.request.ProductCreationRequest;
import com.example.demo.dto.request.ProductSearchRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.security.UserPrincipal;
import com.example.demo.service.ProductLikeService;
import com.example.demo.service.ProductLikeService.ProductLikeSummary;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductLikeService productLikeService;

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> likeProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ProductLikeSummary summary = productLikeService.likeProduct(id, currentUser.getId());
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status(HttpStatus.OK.value())
                .message("Da thich san pham")
                .data(Map.of("liked", summary.liked(), "likeCount", summary.likeCount()))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> unlikeProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ProductLikeSummary summary = productLikeService.unlikeProduct(id, currentUser.getId());
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .status(HttpStatus.OK.value())
                .message("Da bo thich san pham")
                .data(Map.of("liked", summary.liked(), "likeCount", summary.likeCount()))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/liked")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getLikedProducts(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<ProductResponse> data = productLikeService.getLikedProducts(currentUser.getId());
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lay danh sach san pham yeu thich thanh cong")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody ProductCreationRequest request) {
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status(201)
                .message("Tao moi product thanh cong")
                .data(productService.create(request))
                .build();
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @RequestBody ProductUpdateRequest request) {
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status(HttpStatus.OK.value())
                .data(productService.update(id, request))
                .message("Cap nhat san pham thanh cong")
                .build();
        return ResponseEntity.status(HttpStatus.OK.value()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> delete(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .data(productService.delete(id))
                        .message("Xoa thanh cong")
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lay thong tin san pham thanh cong")
                        .data(productService.findById(id, currentUser != null ? currentUser.getId() : null))
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> search(
            ProductSearchRequest request,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<PageResponse<ProductResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tim kiem thanh cong")
                        .data(productService.search(request, pageable, currentUser != null ? currentUser.getId() : null))
                        .build());
    }
}
