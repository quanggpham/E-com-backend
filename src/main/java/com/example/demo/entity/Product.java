package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    @Column(nullable = false)
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @NotBlank(message = "Giá sản phẩm không được để trống")
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Long stockQuantity;

    @Column(name = "thumbnail_url", length = 300)
    private String thumnailUrl;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "create_at")
    private LocalDateTime createdAt;
}
