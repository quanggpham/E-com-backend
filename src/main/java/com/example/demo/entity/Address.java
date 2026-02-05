package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "address_line", nullable = false)
    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String addressLine;

    @Column(name = "city", nullable = false)
    @NotBlank(message = "Thành phố không được để trống")
    private String city;

    @Column(name = "district", nullable = false)
    @NotBlank(message = "Quận không được để trống")
    private String district;

    @Column(name = "is_default")
    private boolean isDefault = false;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;
}
