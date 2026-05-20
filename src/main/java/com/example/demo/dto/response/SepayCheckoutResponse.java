package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class SepayCheckoutResponse {
    private String bankName;
    private String bankAccount;
    private String accountName;
    private BigDecimal amount;
    private String content;
    private String qrUrl;
    private Long orderId;
}
