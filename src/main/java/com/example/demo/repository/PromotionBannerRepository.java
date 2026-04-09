package com.example.demo.repository;

import com.example.demo.entity.PromotionBanner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionBannerRepository extends JpaRepository<PromotionBanner, Long> {

    @Query("""
            select pb
            from PromotionBanner pb
            where pb.active = true
              and (pb.startDate is null or pb.startDate <= :now)
              and (pb.endDate is null or pb.endDate >= :now)
            order by pb.displayOrder asc, pb.id asc
            """)
    List<PromotionBanner> findActivePromotionBanners(@Param("now") LocalDateTime now);

    Page<PromotionBanner> findAllByOrderByDisplayOrderAscIdAsc(Pageable pageable);
}
