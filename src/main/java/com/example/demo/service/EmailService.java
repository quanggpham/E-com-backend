package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${resend.api-key:}")
    private String resendApiKey;

    @Value("${resend.from:}")
    private String resendFrom;

    @Value("${resend.test-recipient:}")
    private String resendTestRecipient;

    @Async
    public void sendOrderConfirmationEmail(Order order) {
        try {
            String recipientEmail = getUserEmail(order.getUser());
            if (recipientEmail == null || recipientEmail.isBlank()) {
                log.warn("Skip order confirmation email because user email is empty for orderId={}", order.getId());
                return;
            }

            Context context = new Context();
            context.setVariable("order", order);

            String htmlContent = templateEngine.process("email/order-confirmation", context);
            sendEmail(recipientEmail, "Order Confirmation", htmlContent, true);
        } catch (Exception e) {
            log.error("Loi khi gui email order confirmation: {}", e.getMessage());
        }
    }

    @Async
    public void sendAbandonedCartEmail(Cart cart, String cartUrl) {
        try {
            String recipientEmail = getUserEmail(cart.getUser());
            if (recipientEmail == null || recipientEmail.isBlank()) {
                log.warn("Skip abandoned cart reminder email because user email is empty for cartId={}", cart.getId());
                return;
            }

            Context context = new Context();
            context.setVariable("cart", cart);
            context.setVariable("user", cart.getUser());
            context.setVariable("cartUrl", cartUrl);
            context.setVariable("itemCount", cart.getItems().size());
            context.setVariable("totalAmount", cart.getItems().stream()
                    .map(item -> item.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

            String htmlContent = templateEngine.process("email/abandoned-cart-reminder", context);
            sendEmail(recipientEmail, "Ban van con mon ngon trong gio hang", htmlContent, true);
        } catch (Exception e) {
            log.error("Loi khi gui email nhac gio hang cartId={}: {}", cart.getId(), e.getMessage());
        }
    }

    @Async
    public void sendReviewRejectedEmail(String email, String productName, String rejectionReason) {
        try {
            String content = """
                    Review cua ban cho san pham "%s" da bi tu choi.
                    Ly do: %s
                    """.formatted(
                    productName,
                    rejectionReason == null ? "Khong duoc cung cap" : rejectionReason
            );
            sendEmail(email, "Review moderation update", content, false);
        } catch (Exception e) {
            log.error("Loi khi gui email tu choi review: {}", e.getMessage());
        }
    }

    @Async
    public void sendPasswordResetEmail(String email, String code) {
        try {
            Context context = new Context();
            context.setVariable("code", code);

            String htmlContent = templateEngine.process("email/password-reset", context);
            sendEmail(email, "[Bếp Việt] Khôi phục mật khẩu tài khoản", htmlContent, true);
        } catch (Exception e) {
            log.error("Loi khi gui email khoi phuc mat khau den {}: {}", email, e.getMessage());
        }
    }

    private void sendEmail(String recipient, String subject, String content, boolean isHtml) {
        if (resendApiKey != null && !resendApiKey.isBlank()) {
            if (resendFrom == null || resendFrom.isBlank()) {
                log.error("Cannot send email via Resend to {} because RESEND_FROM/resend.from is empty", recipient);
                return;
            }

            sendEmailViaResend(recipient, subject, content, isHtml);
            return;
        } else {
            sendEmailViaSmtp(recipient, subject, content, isHtml);
        }
    }

    private void sendEmailViaResend(String recipient, String subject, String content, boolean isHtml) {
        try {
            String resendRecipient = resolveResendRecipient(recipient);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("from", resendFrom);
            body.put("to", List.of(resendRecipient));
            body.put("subject", subject);
            body.put(isHtml ? "html" : "text", content);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.resend.com/emails", entity, String.class);
            log.info("Email sent via Resend API successfully to {} with status {}", resendRecipient, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send email via Resend API to {}", recipient, e);
        }
    }

    private void sendEmailViaSmtp(String recipient, String subject, String content, boolean isHtml) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, isHtml, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(content, isHtml);
            mailSender.send(mimeMessage);
            log.info("Email sent via SMTP successfully to {}", recipient);
        } catch (Exception e) {
            log.error("Failed to send email via SMTP to {}: {}", recipient, e.getMessage());
        }
    }

    private String getUserEmail(User user) {
        return user == null ? null : user.getEmail();
    }

    private String resolveResendRecipient(String recipient) {
        if (resendTestRecipient != null && !resendTestRecipient.isBlank()) {
            log.warn("Resend test recipient is enabled. Original recipient {} is overridden to {}", recipient, resendTestRecipient);
            return resendTestRecipient;
        }
        return recipient;
    }
}
