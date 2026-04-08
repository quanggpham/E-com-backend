package com.example.demo.repository;

import com.example.demo.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("""
            select b
            from Banner b
            where b.active = true
              and (b.startDate is null or b.startDate <= :now)
              and (b.endDate is null or b.endDate >= :now)
            order by b.displayOrder asc, b.id asc
            """)
    List<Banner> findActiveBanners(@Param("now") LocalDateTime now);

    Page<Banner> findAllByOrderByDisplayOrderAscIdAsc(Pageable pageable);
}
