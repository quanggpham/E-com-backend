package com.example.demo.service;


import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.ForgotPasswordRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import com.example.demo.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new BusinessException("Email đã được sử dụng");
        }
        if (userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }

        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        UserPrincipal userPrincipal = UserPrincipal.create(user);

        String jwtToken = jwtService.generateToken(userPrincipal);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(request.getEmail()));

        var userPrincipal = UserPrincipal.create(user);

        var jwtToken = jwtService.generateToken(userPrincipal);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        if (!userRepository.existsByEmail(email)) {
            throw new BusinessException("Email không tồn tại trong hệ thống");
        }

        // Generate 6-digit verification code
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Delete old token if exists, or fetch it
        PasswordResetToken token = passwordResetTokenRepository.findByEmail(email)
                .orElse(new PasswordResetToken());

        token.setEmail(email);
        token.setToken(code);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        passwordResetTokenRepository.save(token);

        // Send email
        emailService.sendPasswordResetEmail(email, code);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        PasswordResetToken token = passwordResetTokenRepository.findByEmailAndToken(email, code)
                .orElseThrow(() -> new BusinessException("Mã xác thực không chính xác hoặc không hợp lệ"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Mã xác thực đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Người dùng không tồn tại"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Clean up reset token
        passwordResetTokenRepository.delete(token);
    }
}
