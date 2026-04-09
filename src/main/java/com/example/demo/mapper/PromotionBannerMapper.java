package com.example.demo.mapper;

import com.example.demo.dto.request.PromotionBannerRequest;
import com.example.demo.dto.response.PromotionBannerResponse;
import com.example.demo.entity.PromotionBanner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PromotionBannerMapper {

    @Mapping(target = "active", source = "isActive")
    PromotionBanner toPromotionBanner(PromotionBannerRequest request);

    PromotionBannerResponse toPromotionBannerResponse(PromotionBanner promotionBanner);

    @Mapping(target = "active", source = "isActive")
    void updatePromotionBannerFromRequest(PromotionBannerRequest request, @MappingTarget PromotionBanner promotionBanner);
}
