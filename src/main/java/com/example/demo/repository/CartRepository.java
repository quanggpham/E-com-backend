package com.example.demo.repository;

import com.example.demo.entity.Cart;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart>findByUser(User user);

    @Query("""
            SELECT DISTINCT c.id
            FROM Cart c
            JOIN c.items i
            WHERE c.updatedAt <= :cutoff
              AND c.user.email IS NOT NULL
              AND TRIM(c.user.email) <> ''
            """)
    List<Long> findReminderCandidateIds(@Param("cutoff") LocalDateTime cutoff);

    @EntityGraph(attributePaths = {"user", "items", "items.product"})
    List<Cart> findByIdIn(List<Long> ids);
}
