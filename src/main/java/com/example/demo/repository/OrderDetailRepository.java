package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @EntityGraph(attributePaths = {"order", "order.user", "product", "review"})
    Optional<OrderDetail> findWithOrderAndProductById(Long id);

    @Query("""
            select od
            from OrderDetail od
            join fetch od.order o
            join fetch od.product p
            left join fetch od.review r
            where o.user.id = :userId
              and o.status = :status
              and r.id is null
            order by o.createdAt desc, od.id desc
            """)
    List<OrderDetail> findReviewableItemsByUserId(@Param("userId") Long userId, @Param("status") OrderStatus status);
}
