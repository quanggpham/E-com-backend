package com.example.demo.repository;

import com.example.demo.entity.CartReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CartReminderLogRepository extends JpaRepository<CartReminderLog, Long> {

    @Query("""
            SELECT COUNT(l) > 0
            FROM CartReminderLog l
            WHERE l.cart.id = :cartId
              AND l.sentAt >= :sentAfter
            """)
    boolean existsRecentReminder(@Param("cartId") Long cartId, @Param("sentAfter") LocalDateTime sentAfter);
}
