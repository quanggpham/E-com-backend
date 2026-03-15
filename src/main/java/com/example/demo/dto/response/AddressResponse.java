package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressResponse {
    private Long id;
    private String recipientName;
    private String phone;
    private String addressLine;
    private String district;
    private String city;
    private boolean isDefault;
    private String fullAddress;
}
