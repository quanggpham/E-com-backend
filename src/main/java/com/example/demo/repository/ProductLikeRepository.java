package com.example.demo.repository;

import com.example.demo.entity.Product;
import com.example.demo.entity.ProductLike;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    Optional<ProductLike> findByUserAndProduct(User user, Product product);
    boolean existsByUserAndProduct(User user, Product product);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    long countByProduct(Product product);
    long countByProductId(Long productId);
    List<ProductLike> findByUserOrderByCreatedAtDesc(User user);
}
