package com.example.demo.dto.response;

import com.example.demo.entity.CartItem;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

//@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmt;
}
