package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.StripeCheckoutResponse;
import com.example.demo.security.UserPrincipal;
import com.example.demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{id}/checkout")
    public ResponseEntity<ApiResponse<?>> sepayCheckout(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .data(paymentService.preparePayment(id, userPrincipal.getId()))
                        .message("...")
                        .status(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @PostMapping("/{id}/stripe/checkout")
    public ResponseEntity<ApiResponse<StripeCheckoutResponse>> stripeCheckout(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<StripeCheckoutResponse>builder()
                        .data(paymentService.prepareStripeCheckout(id, userPrincipal.getId()))
                        .message("Tạo phiên thanh toán Stripe thành công")
                        .status(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @PostMapping("/stripe/webhook")
    public ResponseEntity<ApiResponse<Void>> stripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String stripeSignature
    ) {
        paymentService.handleStripeWebhook(payload, stripeSignature);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Đã xử lý webhook Stripe")
                        .build()
        );
    }
}
