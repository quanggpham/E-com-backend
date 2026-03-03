package com.example.demo.service;

import com.example.demo.entity.Coupon;
import com.example.demo.enums.DiscountType;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CouponRepository;
import com.example.demo.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    public BigDecimal calculateDiscount(String code, BigDecimal amount, Long userId) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã giảm giá"));

        if (!coupon.isActive()) {
            throw new BusinessException("Mã giảm giá không hoạt động");
        }

        if (LocalDate.now().isBefore(coupon.getStartDate()) || LocalDate.now().isAfter(coupon.getExpirationDate())) {
            throw new BusinessException("Mã giảm giá chưa được kích hoạt hoặc đã hết hạn");
        }

        if (coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new BusinessException("Mã giảm giá đã hết");
        }

        if (amount.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new BusinessException("Đơn hàng không đủ số tiền tối thiểu");
        }

        boolean used = couponUsageRepository.existsByCouponIdAndUserId(coupon.getId(), userId);
        if (used) {
            throw new BusinessException("Bạn đã sử dụng mã giảm giá này rồi");
        }

        BigDecimal discountAmount = BigDecimal.ZERO;

        if (coupon.getDiscountType().equals(DiscountType.FIXED_AMOUNT)) {

        }
    }
}
