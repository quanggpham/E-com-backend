package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SePayCheckoutRequest {
    private String merchant;
    private String operation;
    private String payment_method;
    private String order_amount;
    private String currency;
    private String order_invoice_number;
    private String order_description;
    private String success_url;
    private String customer_id;
    private String error_url;
    private String cancel_url;
    private String signature;
}