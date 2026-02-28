package com.example.demo.repository;

import com.example.demo.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cart.user.id = :userId AND c.product.id IN :productIds")
    void deleteByUserIdAndProductIdIn(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);
}
