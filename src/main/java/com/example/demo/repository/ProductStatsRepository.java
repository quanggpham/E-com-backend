package com.example.demo.repository;

import com.example.demo.entity.ProductStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStatsRepository extends JpaRepository<ProductStats, Long> {
}
