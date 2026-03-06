package com.example.demo.controller;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{id}/checkout")
    public ResponseEntity<ApiResponse<?>> sepayCheckout(
            @PathVariable Long id) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .data(paymentService.preparePayment(id))
                        .message("...")
                        .status(HttpStatus.CREATED.value())
                        .build()
        );
    }
}
