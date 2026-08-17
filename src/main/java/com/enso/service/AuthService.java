package com.enso.service;

import com.enso.dto.request.auth.SendOtpRequest;
import com.enso.dto.request.auth.VerifyOtpRequest;
import com.enso.dto.response.auth.AuthResponse;
import com.enso.entity.CartEntity;
import com.enso.entity.UserEntity;
import com.enso.enums.Role;
import com.enso.exception.ResourceNotFoundException;
import com.enso.repository.CartRepository;
import com.enso.repository.UserRepository;
import com.enso.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    @Transactional
    public String sendOtp(SendOtpRequest request) {
        if (!userRepository.existsByMobileNumber(request.getPhone())) {

            UserEntity newUserEntity = UserEntity.builder()
                    .mobileNumber(request.getPhone())
                    .role(Role.ROLE_USER)
                    .build();
            UserEntity savedUserEntity = userRepository.save(newUserEntity);
            CartEntity cartEntity = CartEntity.builder().user(savedUserEntity).build();
            cartRepository.save(cartEntity);
            log.info("Registered new user with phone: {}", request.getPhone());
        }
        return otpService.generateAndStoreOtp(request.getPhone());
    }

    @Transactional(readOnly = true)
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        otpService.validateOtp(request.getPhone(), request.getOtp());

        UserEntity userEntity = userRepository.findByMobileNumber(request.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getPhone()));

        String token = jwtUtil.generateToken(userEntity.getMobileNumber(), userEntity.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(userEntity.getId())
                .phone(userEntity.getMobileNumber())
                .role(userEntity.getRole())
                .build();
    }
}
