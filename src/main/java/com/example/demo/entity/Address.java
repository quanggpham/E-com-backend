package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "addresses")
@SQLDelete(sql = "UPDATE addresses SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recipient_name", nullable = false, length = 100)
    @NotBlank(message = "Họ tên người nhận không được để trống")
    private String recipientName;

    @Column(name = "phone", nullable = false, length = 15)
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng VN")
    private String phone;

    @Column(name = "address_line", nullable = false, columnDefinition = "TEXT")
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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
