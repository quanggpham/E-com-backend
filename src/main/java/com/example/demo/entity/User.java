package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    @NotBlank(message = "username khong duoc de trong")
    @Size(min = 3, max = 50, message = "user phai tu 3 den 50 ki tu")
    private String username;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "mat khau khong duoc de trong")
    private String password;

    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> address;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    @Email(message = "email khong dugn dinh dang")
    @NotBlank(message = "email khong duoc de trong")
    private String email;


    @Column(name = "full_name", nullable = false, length = 100)
    @NotBlank(message = "ho va ten khong duoc de trong")
    private String fullName;

    @Column(name = "phone", length = 15)
//    @Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$",
//            message = "Số điện thoại không hợp lệ")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateAt;

    @Column(name = "deleted_at")
    private LocalDateTime deleteAt;
}
