package com.enso.service;

import com.enso.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private final RedisService redisService;

    public String generateAndStoreOtp(String phone) {
        String otp = String.format("%0" + OTP_LENGTH + "d", new SecureRandom().nextInt(1000000));
        String key = "otp:" + phone;

        redisService.save(key, otp, OTP_TTL);

        log.info("Mock OTP for {}: {}", phone, otp);

        return otp;
    }

    public void validateOtp(String phone, String otp) {

        String key = "otp:" + phone;

        String storedOtp = redisService.get(key);

        if (storedOtp == null) {
            throw new BadRequestException(
                    "No OTP found for this phone number. Please request a new one."
            );
        }

        if (!storedOtp.equals(otp)) {
            throw new BadRequestException("Invalid OTP.");
        }

        redisService.delete(key);
    }
}
