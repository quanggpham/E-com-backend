package com.example.demo.mapper;

import com.example.demo.dto.request.BannerRequest;
import com.example.demo.dto.response.BannerResponse;
import com.example.demo.entity.Banner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BannerMapper {

    @Mapping(target = "active", source = "isActive")
    Banner toBanner(BannerRequest request);

    BannerResponse toBannerResponse(Banner banner);

    @Mapping(target = "active", source = "isActive")
    void updateBannerFromRequest(BannerRequest request, @MappingTarget Banner banner);
}
