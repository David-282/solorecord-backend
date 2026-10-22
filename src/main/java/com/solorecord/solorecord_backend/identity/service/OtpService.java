package com.solorecord.solorecord_backend.identity.service;

import com.solorecord.solorecord_backend.identity.exception.TooManyOtpAttemptsException;
import com.solorecord.solorecord_backend.identity.exception.TooManyOtpRequestsException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;

@Service
@AllArgsConstructor
public class OtpService {

    private static final String OTP_PREFIX = "otp:verification:";
    private static final Duration OTP_TTL = Duration.ofMinutes(10);

    private final RedisTemplate<String, String> redisTemplate;

    public String generateAndSendOtp(String email) {
        String validEmail = email.toLowerCase().strip();
        String otp = generateOtp();
        String key = OTP_PREFIX + validEmail;

        redisTemplate.opsForValue().set(key, otp, OTP_TTL);

        // Stub notification — replace with real email/SMS sending later
        System.out.println("OTP for " + validEmail + ": " + otp);

        return otp;
    }

    public boolean verifyOtp(String email, String submittedOtp) {
        String normalised = email.strip().toLowerCase();
        String attemptsKey = "otp:attempts:" + normalised;

        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, Duration.ofMinutes(10));
        }
        if (attempts != null && attempts > 5) {
            throw new TooManyOtpAttemptsException("Too many attempts, request a new code"); // map to 429
        }

        String key = OTP_PREFIX + normalised;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null || !MessageDigest.isEqual(
                storedOtp.getBytes(StandardCharsets.UTF_8),
                submittedOtp.getBytes(StandardCharsets.UTF_8))) {
            return false;
        }

        redisTemplate.delete(key);
        redisTemplate.delete(attemptsKey);
        return true;
    }

    public void checkRequestLimit(String normalisedEmail) {
        String key = "otp:requests:" + normalisedEmail;
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(15));
        }
        if (count != null && count > 3) {
            throw new TooManyOtpRequestsException("Too many requests, try again later");
        }
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}