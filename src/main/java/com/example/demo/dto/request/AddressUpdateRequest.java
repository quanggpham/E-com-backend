package com.example.demo.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressUpdateRequest {
    @Size(min = 1, max = 100, message = "Họ tên người nhận phải từ 1 đến 100 ký tự")
    private String recipientName;

    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng VN")
    private String phone;

    @Size(min = 1, message = "Địa chỉ chi tiết không được để trống")
    private String addressLine;

    @Size(min = 1, max = 100, message = "Quận/huyện phải từ 1 đến 100 ký tự")
    private String district;

    @Size(min = 1, max = 100, message = "Thành phố/tỉnh phải từ 1 đến 100 ký tự")
    private String city;

    private Boolean isDefault;
}
