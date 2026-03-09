package com.example.demo.controller;

import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreationRequest request) {
        UserResponse data = userService.createUser(request);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Tạo người dùng thành công")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse data = userService.getUserById(id);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin người dùng thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(Pageable pageable) {
        PageResponse<UserResponse> data = userService.getAllUsers(pageable);

        ApiResponse<PageResponse<UserResponse>> response = ApiResponse.<PageResponse<UserResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách người dùng thành công")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse data = userService.updateUser(id, request);
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật người dùng thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Xóa người dùng thành công")
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }
}