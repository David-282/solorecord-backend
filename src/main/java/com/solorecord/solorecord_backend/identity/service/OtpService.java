package com.solorecord.solorecord_backend.identity.service;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@AllArgsConstructor
public class OtpService {

    private static final String OTP_PREFIX = "otp:verification:";
    private static final Duration OTP_TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, String> redisTemplate;

    public String generateAndSendOtp(String email) {
        String otp = generateOtp();
        String key = OTP_PREFIX + email;

        redisTemplate.opsForValue().set(key, otp, OTP_TTL);

        // Stub notification — replace with real email/SMS sending later
        System.out.println("OTP for " + email + ": " + otp);

        return otp;
    }

    public boolean verifyOtp(String email, String submittedOtp) {
        String key = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null || !storedOtp.equals(submittedOtp)) {
            return false;
        }

        redisTemplate.delete(key);
        return true;
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}