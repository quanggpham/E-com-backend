package com.example.demo.repository;

import com.example.demo.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmail(String email);
    Optional<PasswordResetToken> findByEmailAndToken(String email, String token);
    void deleteByEmail(String email);
}
