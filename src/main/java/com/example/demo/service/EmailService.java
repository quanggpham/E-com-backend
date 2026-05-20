package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Async
    public void sendOrderConfirmationEmail(Order order) {
        try {
            String recipientEmail = getUserEmail(order.getUser());
            if (recipientEmail == null || recipientEmail.isBlank()) {
                log.warn("Skip order confirmation email because user email is empty for orderId={}", order.getId());
                return;
            }

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Order Confirmation");

            Context context = new Context();
            context.setVariable("order", order);

            String htmlContent = templateEngine.process("email/order-confirmation", context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Order confirmation email sent to {} for orderId={}", recipientEmail, order.getId());
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

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Ban van con mon ngon trong gio hang");

            Context context = new Context();
            context.setVariable("cart", cart);
            context.setVariable("user", cart.getUser());
            context.setVariable("cartUrl", cartUrl);
            context.setVariable("itemCount", cart.getItems().size());
            context.setVariable("totalAmount", cart.getItems().stream()
                    .map(item -> item.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));

            String htmlContent = templateEngine.process("email/abandoned-cart-reminder", context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Abandoned cart reminder email sent to {} for cartId={}", recipientEmail, cart.getId());
        } catch (Exception e) {
            log.error("Loi khi gui email nhac gio hang cartId={}: {}", cart.getId(), e.getMessage());
        }
    }

    @Async
    public void sendReviewRejectedEmail(String email, String productName, String rejectionReason) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Review moderation update");
            helper.setText("""
                    Review cua ban cho san pham "%s" da bi tu choi.
                    Ly do: %s
                    """.formatted(
                    productName,
                    rejectionReason == null ? "Khong duoc cung cap" : rejectionReason
            ));
            mailSender.send(mimeMessage);
            log.info("Review rejection email sent");
        } catch (Exception e) {
            log.error("Loi khi gui email tu choi review: {}", e.getMessage());
        }
    }

    private String getUserEmail(User user) {
        return user == null ? null : user.getEmail();
    }
}
