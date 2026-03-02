package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {
    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file" )MultipartFile file
            ) throws IOException {
        String imageUrl = cloudinaryService.uploadImage(file);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<String>builder()
                        .status(HttpStatus.OK.value())
                        .message("Tải ảnh lên thành công")
                        .data(imageUrl)
                        .build()
        );
    }
}
