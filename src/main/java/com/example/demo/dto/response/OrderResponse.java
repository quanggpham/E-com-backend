package com.example.demo.dto.response;


import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id; // Mã đơn hàng
    private String fullName;
    private String phoneNumber;
    private String shippingAddress;
    private String note;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private BigDecimal subTotal;
    private BigDecimal discountAmount;
    private BigDecimal totalMoney;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    private List<OrderDetailResponse> orderDetails;
}
