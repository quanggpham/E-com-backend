package com.example.demo.repository;

import com.example.demo.entity.Coupon;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);

    boolean existsByCode(@NotBlank(message = "Mã giảm giá không được để trống") String code);
}
