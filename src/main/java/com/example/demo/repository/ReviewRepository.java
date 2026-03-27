package com.example.demo.repository;

import com.example.demo.entity.Review;
import com.example.demo.enums.ReviewStatus;
import com.example.demo.repository.projection.RatingCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    boolean existsByOrderDetailId(Long orderDetailId);

    Optional<Review> findByOrderDetailId(Long orderDetailId);

    long countByProductIdAndStatus(Long productId, ReviewStatus status);

    @Query("""
            select coalesce(avg(r.rating), 0)
            from Review r
            where r.product.id = :productId
              and r.status = :status
            """)
    Double getAverageRatingByProductIdAndStatus(@Param("productId") Long productId, @Param("status") ReviewStatus status);

    @Query("""
            select r.rating as rating, count(r.id) as total
            from Review r
            where r.product.id = :productId
              and r.status = :status
            group by r.rating
            order by r.rating asc
            """)
    List<RatingCountProjection> getRatingDistributionByProductIdAndStatus(@Param("productId") Long productId,
                                                                          @Param("status") ReviewStatus status);
}
