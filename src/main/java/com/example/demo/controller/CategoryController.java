package com.example.demo.controller;

import com.example.demo.dto.request.CategoryCreationRequest;
import com.example.demo.dto.request.CategoryUpdateRequest;
import com.example.demo.dto.response.CategoryResponse;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.response.ApiResponse;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryCreationRequest request) {
        CategoryResponse categoryResponse = categoryService.create(request);

        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .timestamp(new Date())
                .status(201)
                .message("Tạo danh mục thành công")
                .data(categoryResponse)
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        List<CategoryResponse> categories = categoryService.getAll();
        ApiResponse<List<CategoryResponse>> response = ApiResponse.<List<CategoryResponse>>builder()
                .timestamp(new Date())
                .status(200)
                .message("Lấy danh sách thành công")
                .data(categories)
                .build();
        return ResponseEntity.status(200).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        CategoryResponse categoryResponse = categoryService.getById(id);
        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .timestamp(new Date())
                .status(200)
                .message("Lấy chi tiết category thành công")
                .data(categoryResponse)
                .build();
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable Long id ,@Valid @RequestBody CategoryUpdateRequest request) {
        ApiResponse<CategoryResponse> response = ApiResponse.<CategoryResponse>builder()
                .status(200)
                .data(categoryService.updateById(id, request))
                .timestamp(new Date())
                .message("Update category thành công.")
                .build();
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.status(204).build();
    }
}
