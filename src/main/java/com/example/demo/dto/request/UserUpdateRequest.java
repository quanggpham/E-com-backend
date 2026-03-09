package com.example.demo.dto.request;

import com.example.demo.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "Username phải từ 3 đến 50 ký tự")
    private String username;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    private String phone;
    private Role role;
}