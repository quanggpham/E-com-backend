package com.example.demo.service;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public OrderResponse createOrder(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Order order = Order.builder()
                .user(user)
                .fullName(request.getFullName())
                .note(request.getNote())
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .totalMoney(BigDecimal.ZERO)
                .phoneNumber(request.getPhoneNumber())
                .build();

        BigDecimal totalMoney = BigDecimal.ZERO;

        for (CartItemRequest item : request.getItems())
        {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "", item.getProductId()));
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException("Sản phẩm " + product.getName() + " không đủ số lượng tồn kho");
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            totalMoney = totalMoney.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));


            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.addOrderDetail(orderDetail);
        }

        order.setTotalMoney(totalMoney);

        Order savedOrder = orderRepository.save(order);

        List<Long> productIds = request.getItems().stream()
                .map(CartItemRequest::getProductId)
                .toList();

        cartItemRepository.deleteByUserIdAndProductIdIn(user.getId(), productIds);

        return orderMapper.toOrderResponse(savedOrder);
    }
}
