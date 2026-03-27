package com.example.demo.repository.specification;

import com.example.demo.entity.Review;
import com.example.demo.enums.ReviewStatus;
import org.springframework.data.jpa.domain.Specification;

public final class ReviewSpecification {

    private ReviewSpecification() {
    }

    public static Specification<Review> hasProductId(Long productId) {
        return (root, query, cb) -> productId == null ? null : cb.equal(root.get("product").get("id"), productId);
    }

    public static Specification<Review> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Review> hasRating(Integer rating) {
        return (root, query, cb) -> rating == null ? null : cb.equal(root.get("rating"), rating);
    }

    public static Specification<Review> hasStatus(ReviewStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Review> isApproved() {
        return hasStatus(ReviewStatus.APPROVED);
    }

    public static Specification<Review> pendingOrReported(int minReportCount) {
        return (root, query, cb) -> cb.or(
                cb.equal(root.get("status"), ReviewStatus.PENDING),
                cb.greaterThanOrEqualTo(root.get("reportCount"), minReportCount)
        );
    }

    public static Specification<Review> hasMinReportCount(Integer minReportCount) {
        return (root, query, cb) -> minReportCount == null ? null :
                cb.greaterThanOrEqualTo(root.get("reportCount"), minReportCount);
    }
}
