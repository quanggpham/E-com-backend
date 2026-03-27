package com.example.demo.dto.response;

import com.example.demo.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewableOrderResponse {
    private Long orderId;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<ReviewableOrderItemResponse> items;
}
