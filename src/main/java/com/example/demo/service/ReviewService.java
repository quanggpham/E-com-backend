package com.example.demo.service;

import com.example.demo.dto.request.AdminReviewStatusRequest;
import com.example.demo.dto.request.CreateReviewRequest;
import com.example.demo.dto.request.SellerReplyRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductReviewListResponse;
import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.dto.response.ReviewableOrderItemResponse;
import com.example.demo.dto.response.ReviewableOrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.enums.OrderStatus;
import com.example.demo.enums.ReviewStatus;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.specification.ReviewSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRateLimitService reviewRateLimitService;
    private final ReviewModerationService reviewModerationService;
    private final ProductReviewStatsService productReviewStatsService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public ProductReviewListResponse getProductReviews(Long productId, Integer rating, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 50), pageable.getSort());
        Specification<Review> spec = Specification.where(ReviewSpecification.hasProductId(productId))
                .and(ReviewSpecification.isApproved())
                .and(ReviewSpecification.hasRating(rating));

        Page<Review> pageData = reviewRepository.findAll(spec, safePageable);
        List<ReviewResponse> items = pageData.getContent().stream()
                .map(this::toResponse)
                .toList();

        return ProductReviewListResponse.builder()
                .stats(productReviewStatsService.getStats(productId))
                .reviews(PageResponse.<ReviewResponse>builder()
                        .items(items)
                        .currentPage(pageData.getNumber() + 1)
                        .pageSize(pageData.getSize())
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .build())
                .build();
    }

    @Transactional
    public ReviewResponse createReview(Long userId, CreateReviewRequest request) {
        reviewRateLimitService.checkCreateReviewLimit(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        OrderDetail orderDetail = orderDetailRepository.findWithOrderAndProductById(request.getOrderItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Order detail", "id", request.getOrderItemId()));

        validateReviewEligibility(userId, orderDetail);

        String sanitizedContent = reviewModerationService.sanitize(request.getContent());
        boolean containsBannedKeyword = reviewModerationService.containsBannedKeyword(request.getContent());

        Review review = Review.builder()
                .user(user)
                .product(orderDetail.getProduct())
                .orderDetail(orderDetail)
                .rating(request.getRating())
                .content(sanitizedContent)
                .status(containsBannedKeyword ? ReviewStatus.REJECTED : ReviewStatus.PENDING)
                .rejectionReason(containsBannedKeyword ? "Noi dung chua tu khoa bi cam" : null)
                .build();

        Review savedReview = reviewRepository.saveAndFlush(review);
        productReviewStatsService.refreshStats(savedReview.getProduct().getId());

        return toResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewableOrderResponse> getReviewableOrders(Long userId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findReviewableItemsByUserId(userId, OrderStatus.COMPLETED);

        Map<Long, ReviewableOrderResponseBuilder> grouped = new LinkedHashMap<>();
        for (OrderDetail orderDetail : orderDetails) {
            Order order = orderDetail.getOrder();
            ReviewableOrderResponseBuilder bucket = grouped.computeIfAbsent(order.getId(), ignored ->
                    new ReviewableOrderResponseBuilder(order.getId(), order.getStatus(), order.getCreatedAt()));
            bucket.items.add(ReviewableOrderItemResponse.builder()
                    .orderItemId(orderDetail.getId())
                    .productId(orderDetail.getProduct().getId())
                    .productName(orderDetail.getProduct().getName())
                    .imageUrl(orderDetail.getProduct().getImageUrl())
                    .quantity(orderDetail.getQuantity())
                    .build());
        }

        return grouped.values().stream()
                .map(ReviewableOrderResponseBuilder::build)
                .toList();
    }

    @Transactional
    public ReviewResponse replyToReview(Long reviewId, SellerReplyRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (review.getStatus() != ReviewStatus.APPROVED) {
            throw new BusinessException("Chi co the tra loi review da duoc duyet");
        }
        if (review.getSellerReply() != null && !review.getSellerReply().isBlank()) {
            throw new BusinessException("Review nay da duoc phan hoi");
        }

        review.setSellerReply(reviewModerationService.sanitize(request.getReply()));
        review.setSellerReplyAt(LocalDateTime.now());
        return toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getAdminReviews(Long productId,
                                                        Long userId,
                                                        ReviewStatus status,
                                                        Integer minReportCount,
                                                        Pageable pageable) {
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 50), pageable.getSort());
        Specification<Review> spec = Specification.where(ReviewSpecification.hasProductId(productId))
                .and(ReviewSpecification.hasUserId(userId))
                .and(ReviewSpecification.hasStatus(status))
                .and(ReviewSpecification.hasMinReportCount(minReportCount));

        Page<Review> pageData = reviewRepository.findAll(spec, safePageable);
        return PageResponse.<ReviewResponse>builder()
                .items(pageData.getContent().stream().map(this::toResponse).toList())
                .currentPage(pageData.getNumber() + 1)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getAdminPendingOrReportedReviews(Long productId,
                                                                         Long userId,
                                                                         Integer minReportCount,
                                                                         Pageable pageable) {
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 50), pageable.getSort());
        int reportThreshold = minReportCount == null ? 3 : minReportCount;

        Specification<Review> spec = Specification.where(ReviewSpecification.pendingOrReported(reportThreshold))
                .and(ReviewSpecification.hasProductId(productId))
                .and(ReviewSpecification.hasUserId(userId));

        Page<Review> pageData = reviewRepository.findAll(spec, safePageable);
        return PageResponse.<ReviewResponse>builder()
                .items(pageData.getContent().stream().map(this::toResponse).toList())
                .currentPage(pageData.getNumber() + 1)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .build();
    }

    @Transactional
    public ReviewResponse updateReviewStatus(Long reviewId, AdminReviewStatusRequest request) {
        if (request.getStatus() != ReviewStatus.APPROVED && request.getStatus() != ReviewStatus.REJECTED) {
            throw new BusinessException("Admin chi duoc approve hoac reject review");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        ReviewStatus previousStatus = review.getStatus();
        review.setStatus(request.getStatus());
        if (request.getStatus() == ReviewStatus.REJECTED) {
            if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
                throw new BusinessException("Can cung cap ly do tu choi review");
            }
            review.setRejectionReason(reviewModerationService.sanitize(request.getRejectionReason()));
        } else {
            review.setRejectionReason(null);
        }

        Review savedReview = reviewRepository.saveAndFlush(review);

        if (previousStatus == ReviewStatus.APPROVED || request.getStatus() == ReviewStatus.APPROVED) {
            productReviewStatsService.refreshStats(savedReview.getProduct().getId());
        }
        if (request.getStatus() == ReviewStatus.REJECTED) {
            emailService.sendReviewRejectedEmail(
                    savedReview.getUser().getEmail(),
                    savedReview.getProduct().getName(),
                    savedReview.getRejectionReason()
            );
        }

        return toResponse(savedReview);
    }

    private void validateReviewEligibility(Long userId, OrderDetail orderDetail) {
        if (!orderDetail.getOrder().getUser().getId().equals(userId)) {
            throw new BusinessException("Ban khong so huu order item nay");
        }
        if (orderDetail.getOrder().getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessException("Chi co the review san pham trong don hang da hoan thanh");
        }
        if (reviewRepository.existsByOrderDetailId(orderDetail.getId())) {
            throw new BusinessException("Order item nay da duoc danh gia");
        }
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .orderItemId(review.getOrderDetail().getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .rating(review.getRating())
                .content(review.getContent())
                .status(review.getStatus())
                .reportCount(review.getReportCount())
                .sellerReply(review.getSellerReply())
                .rejectionReason(review.getRejectionReason())
                .sellerReplyAt(review.getSellerReplyAt())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    private static final class ReviewableOrderResponseBuilder {
        private final Long orderId;
        private final OrderStatus status;
        private final LocalDateTime createdAt;
        private final List<ReviewableOrderItemResponse> items = new ArrayList<>();

        private ReviewableOrderResponseBuilder(Long orderId, OrderStatus status, LocalDateTime createdAt) {
            this.orderId = orderId;
            this.status = status;
            this.createdAt = createdAt;
        }

        private ReviewableOrderResponse build() {
            return ReviewableOrderResponse.builder()
                    .orderId(orderId)
                    .status(status)
                    .createdAt(createdAt)
                    .items(items)
                    .build();
        }
    }
}
