package com.example.demo.service;

import com.example.demo.dto.request.SePayCheckoutRequest;
import com.example.demo.entity.Order;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrderRepository;
import com.example.demo.utils.SePayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    @Value("${sepay.merchant-id}")
    private String merchantId;

    @Value("${sepay.secret-key}")
    private String secretKey;

    public SePayCheckoutRequest preparePayment(Long orderId) throws Exception {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        String finalAmount = order.getTotalMoney()
                .setScale(0, RoundingMode.HALF_UP) // 181856.80 -> 181857
                .toPlainString();

        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("merchant", merchantId);
        fields.put("operation", "PURCHASE");
        fields.put("payment_method", "BANK_TRANSFER");
        fields.put("order_amount", finalAmount);
        fields.put("currency", "VND");
        fields.put("order_invoice_number", "INV-" + order.getId());
        fields.put("order_description", "Thanh toan don hang #" + order.getId());
        fields.put("success_url", "https://your-domain.com/payment/success");
        fields.put("customer_id", "CUST_005");
        fields.put("error_url", "https://your-domain.com/payment/error");
        fields.put("cancel_url", "https://your-domain.com/payment/cancel");

        String signature = SePayUtils.makeSignature(fields, secretKey);

        return SePayCheckoutRequest.builder()
                .merchant(fields.get("merchant"))
                .operation(fields.get("operation"))
                .payment_method(fields.get("payment_method"))
                .order_amount(fields.get("order_amount"))
                .currency(fields.get("currency"))
                .order_invoice_number(fields.get("order_invoice_number"))
                .order_description(fields.get("order_description"))
                .success_url(fields.get("success_url"))
                .error_url(fields.get("error_url"))
                .customer_id(fields.get("customer_id"))
                .cancel_url(fields.get("cancel_url"))
                .signature(signature)
                .build();
    }
}
