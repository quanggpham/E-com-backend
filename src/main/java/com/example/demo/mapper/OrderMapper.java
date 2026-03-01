package com.example.demo.mapper;

import com.example.demo.dto.response.OrderDetailResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "product.name", target = "name")
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);
}
