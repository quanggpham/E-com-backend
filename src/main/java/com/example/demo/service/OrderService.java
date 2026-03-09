package com.example.demo.service;

import com.example.demo.dto.request.CartItemRequest;
import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.AccessDeniedException;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CouponService couponService;
    private final EmailService emailService;

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
                .subTotal(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .shippingAddress(request.getShippingAddress())
                .totalMoney(BigDecimal.ZERO)
                .phoneNumber(request.getPhoneNumber())
                .build();

        BigDecimal subTotal = BigDecimal.ZERO;

        for (CartItemRequest item : request.getItems())
        {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "", item.getProductId()));
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BusinessException("Sản phẩm " + product.getName() + " không đủ số lượng tồn kho");
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            subTotal = subTotal.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));


            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.addOrderDetail(orderDetail);
        }

        order.setSubTotal(subTotal);

        String code = request.getCode();
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (code != null && !code.trim().isEmpty()) {
            discountAmount = couponService.calculateDiscount(request.getCode(), subTotal, userId);
        }

        BigDecimal totalMoney = subTotal.subtract(discountAmount);
        order.setTotalMoney(totalMoney);
        order.setDiscountAmount(discountAmount);

        Order savedOrder = orderRepository.save(order);

        if (code != null && !code.trim().isEmpty() && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            couponService.markCouponAsUsed(code, user, savedOrder, discountAmount);
        }
        
        List<Long> productIds = request.getItems().stream()
                .map(CartItemRequest::getProductId)
                .toList();

        cartItemRepository.deleteByUserIdAndProductIdIn(user.getId(), productIds);

        emailService.sendOrderConfirmationEmail(savedOrder);

        return orderMapper.toOrderResponse(savedOrder);
    }

    public PageResponse<OrderResponse> getAll(Pageable pageable) {
        int size = pageable.getPageSize();
        int validPageSize= Math.min(size, 50);

         Pageable newPageable = PageRequest.of(pageable.getPageNumber(), validPageSize, pageable.getSort());

        Page<Order> pageData= orderRepository.findAll(newPageable);
        List<OrderResponse> response= pageData.getContent().stream()
                .map(orderMapper::toOrderResponse)
                .toList();

        return PageResponse.<OrderResponse>builder()
                .items(response)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .currentPage(pageData.getNumber() + 1)
                .build();
    }

    public OrderResponse getById(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập");
        }

        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập");
        }

        if (!order.getStatus().equals(OrderStatus.PENDING)) {
            throw new BusinessException("Chỉ đơn hàng đang chờ xác nhận mới có thể hủy");
        }
        order.setStatus(OrderStatus.CANCELLED);
        List<OrderDetail> orderDetails = order.getOrderDetails();

        for (OrderDetail item : orderDetails) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        orderRepository.save(order);
    }

    public PageResponse<OrderResponse> getAllByUserId(Long userId, Pageable pageable) {
        int size = pageable.getPageSize();
        int validPageSize= Math.min(size, 50);

        Pageable finalPageable = PageRequest.of(pageable.getPageNumber(), validPageSize, pageable.getSort());
        Page<Order> pageData= orderRepository.findAllByUserId(userId, finalPageable);

        List<OrderResponse> responses =  pageData.getContent().stream()
                .map(orderMapper::toOrderResponse)
                .toList();

        return PageResponse.<OrderResponse>builder()
                .currentPage(pageData.getNumber() + 1)
                .items(responses)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .build();
    }

    public OrderResponse adminGetById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hagnf"));

        order.setStatus(orderStatus);
        orderRepository.save(order);
    }
}
