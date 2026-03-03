
package com.example.demo.service;

import com.example.demo.entity.Order;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendOrderConfirmationEmail(Order order) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("phamquangdung188@gmail.com");
            helper.setTo("ptyn.18904111@gmail.com");
            helper.setSubject("Order Confirmation");

            Context context = new Context();
            context.setVariable("order", order);

            String htmlContent = templateEngine.process("email/order-confirmation", context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            log.info("Order confirmation email sent");
        } catch (Exception e) {
            log.error("Lỗi khi gửi email: {}", e.getMessage());
        }
    }
}
