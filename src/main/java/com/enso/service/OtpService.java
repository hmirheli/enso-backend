package com.enso.service;

import com.enso.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RedisService redisService;
    private final SmsService smsService;

    public void generateAndStoreOtp(String phone) {
        String otp = String.format(Locale.ROOT,
                "%0" + OTP_LENGTH + "d",
                SECURE_RANDOM.nextInt(1_000_000));

        String key = "otp:" + phone;
        redisService.save(key, otp, OTP_TTL);

        try {
            smsService.sendOtp(phone, otp);
        } catch (RuntimeException exception) {
            // کدی که پیامکش ارسال نشده نباید قابل تأیید باقی بماند.
            redisService.delete(key);
            throw exception;
        }
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
