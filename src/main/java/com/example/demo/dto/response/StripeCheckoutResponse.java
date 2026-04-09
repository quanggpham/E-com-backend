package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StripeCheckoutResponse {
    private String sessionId;
    private String checkoutUrl;
    private String publishableKey;
}
