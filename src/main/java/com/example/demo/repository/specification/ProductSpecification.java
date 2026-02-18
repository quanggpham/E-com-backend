package com.example.demo.repository.specification;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    public static Specification<Product> hasName(String name) {
        return (root, query, cb) -> (name == null || name.isEmpty()) ? null : cb.like(root.get("name").as(String.class), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> (categoryId == null) ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null & maxPrice == null) {
                return null;
            }
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price").as(BigDecimal.class), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price").as(BigDecimal.class), minPrice);
            }
            return cb.lessThanOrEqualTo(root.get("price").as(BigDecimal.class), maxPrice);
        };
    }
}
