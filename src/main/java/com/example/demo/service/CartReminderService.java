package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.CartReminderLog;
import com.example.demo.repository.CartReminderLogRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartReminderService {

    private final CartRepository cartRepository;
    private final CartReminderLogRepository cartReminderLogRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    @Value("${app.cart-reminder.enabled:true}")
    private boolean cartReminderEnabled;

    @Value("${app.cart-reminder.delay-minutes:60}")
    private long delayMinutes;

    @Value("${app.cart-reminder.cooldown-hours:24}")
    private long cooldownHours;

    @Value("${app.cart-url:http://localhost:3000/cart}")
    private String cartUrl;

    @Scheduled(
            fixedDelayString = "${app.cart-reminder.fixed-delay-ms:1800000}",
            initialDelayString = "${app.cart-reminder.initial-delay-ms:300000}"
    )
    @Transactional
    public void sendAbandonedCartReminders() {
        if (!cartReminderEnabled) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusMinutes(delayMinutes);
        LocalDateTime reminderCooldown = now.minusHours(cooldownHours);

        List<Long> candidateIds = cartRepository.findReminderCandidateIds(cutoff);
        if (candidateIds.isEmpty()) {
            return;
        }

        List<Cart> carts = cartRepository.findByIdIn(candidateIds);
        for (Cart cart : carts) {
            if (!isEligible(cart, reminderCooldown)) {
                continue;
            }

            emailService.sendAbandonedCartEmail(cart, cartUrl);
            cartReminderLogRepository.save(CartReminderLog.builder()
                    .cart(cart)
                    .user(cart.getUser())
                    .build());
            log.info("Queued abandoned cart reminder email for cartId={}, userId={}", cart.getId(), cart.getUser().getId());
        }
    }

    private boolean isEligible(Cart cart, LocalDateTime reminderCooldown) {
        if (cart.getUser() == null || cart.getUser().getEmail() == null || cart.getUser().getEmail().isBlank()) {
            return false;
        }
        if (cartReminderLogRepository.existsRecentReminder(cart.getId(), reminderCooldown)) {
            return false;
        }
        if (orderRepository.existsByUserIdAndCreatedAtAfter(cart.getUser().getId(), cart.getUpdatedAt())) {
            return false;
        }

        return cart.getItems().stream().anyMatch(this::isValidReminderItem);
    }

    private boolean isValidReminderItem(CartItem item) {
        return item.getProduct() != null
                && Boolean.TRUE.equals(item.getProduct().getIsActive())
                && item.getProduct().getStockQuantity() != null
                && item.getProduct().getStockQuantity() > 0
                && item.getQuantity() != null
                && item.getQuantity() > 0;
    }
}
