package com.example.demo.mapper;

import com.example.demo.dto.request.CouponRequest;
import com.example.demo.dto.response.CouponResponse;
import com.example.demo.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CouponMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "product", ignore = true)
    Coupon toCoupon(CouponRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "productId", source = "product.id")
    CouponResponse toCouponResponse(Coupon coupon);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateCouponFromRequest(CouponRequest request, @MappingTarget Coupon coupon);
}
