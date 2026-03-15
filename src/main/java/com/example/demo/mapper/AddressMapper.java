package com.example.demo.mapper;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.request.AddressUpdateRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.entity.Address;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Address toEntity(AddressRequest request);

    @Mapping(target = "fullAddress", expression = "java(toFullAddress(address))")
    @Mapping(target = "isDefault", expression = "java(address.isDefault())")
    AddressResponse toResponse(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateFromRequest(AddressUpdateRequest request, @MappingTarget Address address);

    default String toFullAddress(Address address) {
        if (address == null) return null;
        return String.format("%s, %s, %s", address.getAddressLine(), address.getDistrict(), address.getCity());
    }
}
