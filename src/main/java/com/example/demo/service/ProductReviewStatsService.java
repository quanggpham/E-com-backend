package com.example.demo.service;

import com.example.demo.dto.response.ProductReviewStatsResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStats;
import com.example.demo.enums.ReviewStatus;
import com.example.demo.event.ProductReviewStatsRefreshEvent;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductStatsRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.projection.RatingCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductReviewStatsService {

    private final ProductStatsRepository productStatsRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public ProductReviewStatsResponse getStats(Long productId) {
        long approvedReviews = reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        ProductStats stats = productStatsRepository.findById(productId).orElse(null);

        if (stats == null || !Long.valueOf(approvedReviews).equals(stats.getTotalReviews())) {
            refreshStats(productId);
            stats = productStatsRepository.findById(productId).orElse(null);
        }

        if (stats == null) {
            return ProductReviewStatsResponse.builder()
                    .productId(productId)
                    .avgRating(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                    .totalReviews(0L)
                    .ratingDistribution(defaultDistribution())
                    .build();
        }

        return toResponse(stats);
    }

    @Async
    @TransactionalEventListener
    public void handleRefresh(ProductReviewStatsRefreshEvent event) {
        refreshStats(event.productId());
    }

    @Transactional
    public void refreshStats(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found for stats refresh"));

        long totalReviews = reviewRepository.countByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        Double average = reviewRepository.getAverageRatingByProductIdAndStatus(productId, ReviewStatus.APPROVED);
        List<RatingCountProjection> rawDistribution =
                reviewRepository.getRatingDistributionByProductIdAndStatus(productId, ReviewStatus.APPROVED);

        Map<String, Long> distribution = defaultDistribution();
        for (RatingCountProjection item : rawDistribution) {
            distribution.put(String.valueOf(item.getRating()), item.getTotal());
        }

        ProductStats stats = productStatsRepository.findById(productId)
                .orElseGet(() -> ProductStats.builder()
                        .product(product)
                        .build());
        stats.setAvgRating(BigDecimal.valueOf(average == null ? 0D : average).setScale(2, RoundingMode.HALF_UP));
        stats.setTotalReviews(totalReviews);
        stats.setRatingDistribution(distribution);
        productStatsRepository.save(stats);
    }

    private ProductReviewStatsResponse toResponse(ProductStats stats) {
        return ProductReviewStatsResponse.builder()
                .productId(stats.getProductId())
                .avgRating(stats.getAvgRating())
                .totalReviews(stats.getTotalReviews())
                .ratingDistribution(stats.getRatingDistribution())
                .build();
    }

    private Map<String, Long> defaultDistribution() {
        Map<String, Long> distribution = new LinkedHashMap<>();
        for (int rating = 1; rating <= 5; rating++) {
            distribution.put(String.valueOf(rating), 0L);
        }
        return distribution;
    }
}
