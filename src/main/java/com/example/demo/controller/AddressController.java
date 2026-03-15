package com.example.demo.controller;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.request.AddressUpdateRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.security.UserPrincipal;
import com.example.demo.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<AddressResponse> data = addressService.getAddressesByUserId(currentUser.getId());
        ApiResponse<List<AddressResponse>> response = ApiResponse.<List<AddressResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách địa chỉ thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        AddressResponse data = addressService.getById(id, currentUser.getId());
        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin địa chỉ thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse data = addressService.create(currentUser.getId(), request);
        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Thêm địa chỉ thành công")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefault(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        AddressResponse data = addressService.setDefault(id, currentUser.getId());
        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Đặt địa chỉ mặc định thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody AddressUpdateRequest request
    ) {
        AddressResponse data = addressService.update(id, currentUser.getId(), request);
        ApiResponse<AddressResponse> response = ApiResponse.<AddressResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Cập nhật địa chỉ thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        addressService.delete(id, currentUser.getId());
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Xóa địa chỉ thành công")
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

}
