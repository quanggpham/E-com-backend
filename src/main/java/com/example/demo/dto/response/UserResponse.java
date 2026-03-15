package com.example.demo.dto.response;

import com.example.demo.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String imageUrl;
    private String address;
    private Role role;
    private LocalDateTime createAt;
}