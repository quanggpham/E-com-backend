package com.example.demo.controller;

import com.example.demo.dto.request.ProductCreationRequest;
import com.example.demo.dto.request.ProductSearchRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody ProductCreationRequest request) {
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status(201)
                .message("Tạo mới product thành công")
                .data(productService.create(request))
                .build();
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id,@RequestBody ProductUpdateRequest request) {
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
                .status(HttpStatus.OK.value())
                .data(productService.update(id, request))
                .message("Cập nhật sản phẩm thành công")
                .build();
        return  ResponseEntity.status(HttpStatus.OK.value()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> delete(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .data(productService.delete(id))
                        .message("Xóa thành công")
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.<ProductResponse>builder()
                        .status(HttpStatus.OK.value())
                        .message("Lấy thông tin sản phẩm thành công")
                        .data(productService.findById(id))
                        .build());
    }

//    @GetMapping
//    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAll(
//            @RequestParam(defaultValue = "1") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDirection,
//            @RequestParam(required = false) String name
//    ) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponse.<PageResponse<ProductResponse>>builder()
//                        .data(productService.findAll(page, size, sortBy, sortDirection, name))
//                        .message("Lấy sản phẩm thành công")
//                        .status(HttpStatus.OK.value())
//                        .build());
//    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> search(ProductSearchRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<PageResponse<ProductResponse>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tìm kiếm thành công")
                        .data(productService.search(request))
                        .build());
    }
}
