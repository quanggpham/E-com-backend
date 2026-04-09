package com.example.demo.repository;

import com.example.demo.entity.Coupon;
import com.example.demo.enums.PromotionType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, JpaSpecificationExecutor<Coupon> {
    Optional<Coupon> findByCode(String code);

    boolean existsByCode(@NotBlank(message = "Mã giảm giá không được để trống") String code);

    Page<Coupon> findAllByPromotionType(PromotionType promotionType, Pageable pageable);
}
