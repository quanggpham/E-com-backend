package com.example.demo.mapper;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.CouponResponse;
import com.example.demo.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    Coupon toCoupon(CouponRequest request);

    CouponResponse toCouponResponse(Coupon coupon);

    void updateCouponFromRequest(CouponRequest request, @MappingTarget Coupon coupon);
}