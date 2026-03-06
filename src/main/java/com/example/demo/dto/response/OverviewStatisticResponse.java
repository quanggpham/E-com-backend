package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class OverviewStatisticResponse {
    private Long totalOrders;
    private BigDecimal totalRevenue;
}
