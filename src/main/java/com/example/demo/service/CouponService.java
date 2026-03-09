package com.example.demo.service;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.CouponResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.Coupon;
import com.example.demo.entity.CouponUsage;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.enums.DiscountType;
import com.example.demo.exception.BaseException;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CouponMapper;
import com.example.demo.repository.CouponRepository;
import com.example.demo.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final CouponMapper  couponMapper;

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
            discountAmount = coupon.getDiscountValue();
        } else if (coupon.getDiscountType().equals(DiscountType.PERCENTAGE)){
            BigDecimal percent = coupon.getDiscountValue().divide(new BigDecimal(100));
            discountAmount = percent.multiply(amount);

            if (coupon.getMaxDiscountAmount() != null && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
                discountAmount = coupon.getMaxDiscountAmount();
            }
        }

        if (discountAmount.compareTo(amount) > 0) {
            discountAmount = amount;
        }

        return discountAmount;
    }

    @Transactional
    public void markCouponAsUsed(String code, User user, Order order, BigDecimal appliedDiscount) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã giảm giá"));

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        CouponUsage couponUsage = CouponUsage.builder()
                .coupon(coupon)
                .user(user)
                .order(order)
                .discountAmount(appliedDiscount)
                .build();
        couponUsageRepository.save(couponUsage);
    }

    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new BaseException("Mã giảm giá đã tồn tại: " + request.getCode());
        }

        validateCouponLogic(request);

        Coupon coupon = couponMapper.toCoupon(request);
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setUsedCount(0);
//        coupon.setActive(true);
        return couponMapper.toCouponResponse(couponRepository.save(coupon));
    }

    @Transactional(readOnly = true)
    public PageResponse<CouponResponse> getAllCoupons(Pageable pageable) {
        int validPageSize = Math.min(pageable.getPageSize(), 50);
        Pageable newPageable = PageRequest.of(pageable.getPageNumber(), validPageSize, pageable.getSort());

        Page<Coupon> pageData = couponRepository.findAll(newPageable);

        List<CouponResponse> response = pageData.getContent().stream()
                .map(couponMapper::toCouponResponse)
                .toList();

        return PageResponse.<CouponResponse>builder()
                .items(response)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageData.getNumber() + 1)
                .build();
    }

    @Transactional
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã giảm giá"));

        if (!coupon.getCode().equalsIgnoreCase(request.getCode()) && couponRepository.existsByCode(request.getCode())) {
            throw new BaseException("Mã giảm giá mới đã tồn tại trong hệ thống");
        }

        validateCouponLogic(request);

        couponMapper.updateCouponFromRequest(request, coupon);
        coupon.setCode(request.getCode().toUpperCase());

        return couponMapper.toCouponResponse(couponRepository.save(coupon));
    }

    @Transactional(readOnly = true)
    public CouponResponse getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã giảm giá với ID: " + id));
        return couponMapper.toCouponResponse(coupon);
    }

    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy mã giảm giá để xóa");
        }
        couponRepository.deleteById(id);
    }

    private void validateCouponLogic(CouponRequest request) {
        if (request.getStartDate().isAfter(request.getExpirationDate())) {
            throw new BaseException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }

        if (request.getDiscountType() == DiscountType.PERCENTAGE) {
            if (request.getDiscountValue().compareTo(new java.math.BigDecimal("100")) > 0) {
                throw new BaseException("Giá trị giảm theo phần trăm không được vượt quá 100");
            }
            if (request.getMaxDiscountAmount() == null) {
                throw new BaseException("Vui lòng thiết lập số tiền giảm tối đa (maxDiscountAmount) cho mã giảm theo %");
            }
        }
    }

    @Transactional
    public void changeCouponStatus(Long id, boolean active) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã giảm giá"));

        coupon.setActive(active);
        couponRepository.save(coupon);
    }
}
