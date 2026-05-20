package com.example.demo.service;

import com.example.demo.dto.request.SePayCheckoutRequest;
import com.example.demo.dto.response.StripeCheckoutResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.Payment;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.utils.SePayUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Value("${sepay.merchant-id}")
    private String merchantId;

    @Value("${sepay.secret-key}")
    private String secretKey;

    @Value("${stripe.secret-key:}")
    private String stripeSecretKey;

    @Value("${stripe.publishable-key:}")
    private String stripePublishableKey;

    @Value("${stripe.webhook-secret:}")
    private String stripeWebhookSecret;

    @Value("${stripe.success-url}")
    private String stripeSuccessUrl;

    @Value("${stripe.cancel-url}")
    private String stripeCancelUrl;

    public SePayCheckoutRequest preparePayment(Long orderId, Long userId) throws Exception {
        Order order = findOwnedOrder(orderId, userId);
        String finalAmount = order.getTotalMoney()
                .setScale(0, RoundingMode.HALF_UP)
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

    @Transactional
    public StripeCheckoutResponse prepareStripeCheckout(Long orderId, Long userId) throws StripeException {
        ensureStripeConfigured();

        Order order = findOwnedOrder(orderId, userId);

        if (order.getPaymentMethod() != PaymentMethod.STRIPE) {
            throw new BusinessException("Đơn hàng này không sử dụng phương thức thanh toán Stripe");
        }

        Payment existingPayment = paymentRepository.findByOrderIdAndPaymentMethod(orderId, PaymentMethod.STRIPE)
                .orElse(null);

        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.COMPLETED) {
            throw new BusinessException("Đơn hàng này đã được thanh toán thành công");
        }

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(stripeSuccessUrl)
                .setCancelUrl(stripeCancelUrl)
                .setClientReferenceId(String.valueOf(order.getId()))
                .putMetadata("orderId", String.valueOf(order.getId()))
                .putMetadata("paymentMethod", PaymentMethod.STRIPE.name())
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .putMetadata("orderId", String.valueOf(order.getId()))
                                .putMetadata("paymentMethod", PaymentMethod.STRIPE.name())
                                .build()
                )
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("vnd")
                                                .setUnitAmount(toStripeAmount(order.getTotalMoney()))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Thanh toán đơn hàng #" + order.getId())
                                                                .setDescription("Don hang #" + order.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        Payment payment = existingPayment != null ? existingPayment : Payment.builder()
                .order(order)
                .paymentMethod(PaymentMethod.STRIPE)
                .amount(order.getTotalMoney())
                .status(PaymentStatus.PENDING)
                .build();

        payment.setTransactionReference(session.getId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setGatewayResponseJson(session.toJson());
        paymentRepository.save(payment);

        return StripeCheckoutResponse.builder()
                .sessionId(session.getId())
                .checkoutUrl(session.getUrl())
                .publishableKey(stripePublishableKey)
                .build();
    }

    @Transactional
    public void handleStripeWebhook(String payload, String stripeSignature) {
        ensureStripeWebhookConfigured();

        try {
            Webhook.constructEvent(payload, stripeSignature, stripeWebhookSecret);
        } catch (SignatureVerificationException ex) {
            throw new BusinessException("Stripe webhook signature không hợp lệ");
        } catch (Exception ex) {
            throw new BusinessException("Payload webhook Stripe không hợp lệ");
        }

        JsonNode root = parseWebhookPayload(payload);
        String eventType = readText(root, "type");

        log.info("Nhan Stripe webhook event: {}", eventType);

        switch (eventType) {
            case "checkout.session.completed" -> handleCheckoutSessionCompleted(root, payload);
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(root, payload);
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed(root, payload);
            default -> log.info("Bo qua Stripe event type: {}", eventType);
        }
    }

    private void handleCheckoutSessionCompleted(JsonNode root, String payload) {
        JsonNode sessionNode = root.path("data").path("object");
        String sessionId = readText(sessionNode, "id");
        String orderIdValue = readText(sessionNode.path("metadata"), "orderId");

        log.info("Xu ly checkout.session.completed: sessionId={}, orderId={}", sessionId, orderIdValue);

        if (sessionId == null || orderIdValue == null) {
            log.warn("Thieu sessionId/orderId trong checkout.session.completed");
            return;
        }

        Payment payment = paymentRepository.findByTransactionReference(sessionId)
                .orElseGet(() -> createStripePayment(orderIdValue, sessionId, payload));
        markStripePaymentCompleted(payment, payload);
    }

    private void handlePaymentIntentSucceeded(JsonNode root, String payload) {
        JsonNode paymentIntentNode = root.path("data").path("object");
        String orderIdValue = readText(paymentIntentNode.path("metadata"), "orderId");

        log.info("Xu ly payment_intent.succeeded: orderId={}", orderIdValue);

        if (orderIdValue == null) {
            log.warn("Thieu orderId trong payment_intent.succeeded");
            return;
        }

        Long orderId = Long.valueOf(orderIdValue);
        Payment payment = paymentRepository.findByOrderIdAndPaymentMethod(orderId, PaymentMethod.STRIPE)
                .orElse(null);
        if (payment == null) {
            log.warn("Khong tim thay payment STRIPE cho orderId={} khi xu ly payment_intent.succeeded", orderId);
            return;
        }
        markStripePaymentCompleted(payment, payload);
    }

    private void handlePaymentIntentFailed(JsonNode root, String payload) {
        JsonNode paymentIntentNode = root.path("data").path("object");
        String orderIdValue = readText(paymentIntentNode.path("metadata"), "orderId");

        log.info("Xu ly payment_intent.payment_failed: orderId={}", orderIdValue);

        if (orderIdValue == null) {
            log.warn("Thieu orderId trong payment_intent.payment_failed");
            return;
        }

        Long orderId = Long.valueOf(orderIdValue);
        Payment payment = paymentRepository.findByOrderIdAndPaymentMethod(orderId, PaymentMethod.STRIPE)
                .orElse(null);
        if (payment == null) {
            log.warn("Khong tim thay payment STRIPE cho orderId={} khi xu ly payment_intent.payment_failed", orderId);
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setGatewayResponseJson(payload);
        paymentRepository.save(payment);
    }

    private Payment createStripePayment(String orderIdValue, String sessionId, String payload) {
        Long orderId = Long.valueOf(orderIdValue);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        return Payment.builder()
                .order(order)
                .paymentMethod(PaymentMethod.STRIPE)
                .amount(order.getTotalMoney())
                .transactionReference(sessionId)
                .status(PaymentStatus.PENDING)
                .gatewayResponseJson(payload)
                .build();
    }

    private void markStripePaymentCompleted(Payment payment, String payload) {
        boolean shouldSendEmail = payment.getStatus() != PaymentStatus.COMPLETED
                || payment.getOrder().getStatus() != OrderStatus.CONFIRMED;

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setGatewayResponseJson(payload);
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        if (shouldSendEmail) {
            Order emailReadyOrder = orderRepository.findByIdWithEmailDetails(order.getId())
                    .orElse(order);
            emailService.sendOrderConfirmationEmail(emailReadyOrder);
        }
    }

    private Long toStripeAmount(BigDecimal amount) {
        return amount.setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private Order findOwnedOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền thao tác với đơn hàng này");
        }
        return order;
    }

    private void ensureStripeConfigured() {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new BusinessException("Stripe secret key chưa được cấu hình");
        }
    }

    private void ensureStripeWebhookConfigured() {
        if (stripeWebhookSecret == null || stripeWebhookSecret.isBlank()) {
            throw new BusinessException("Stripe webhook secret chưa được cấu hình");
        }
    }

    private JsonNode parseWebhookPayload(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (Exception ex) {
            throw new BusinessException("Không thể parse payload webhook Stripe");
        }
    }

    private String readText(JsonNode node, String fieldName) {
        JsonNode valueNode = node.path(fieldName);
        if (valueNode.isMissingNode() || valueNode.isNull() || valueNode.asText().isBlank()) {
            return null;
        }
        return valueNode.asText();
    }
}
