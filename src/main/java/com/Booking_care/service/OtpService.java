package com.Booking_care.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Otp;
import com.Booking_care.repository.OtpRepository;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private static final SecureRandom random = new SecureRandom();

    @Value("${spring.email.otp.expiry-time-seconds}")
    private String expirySeconds;

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public Otp generateOtp4Digits(String email) {
        int number = random.nextInt(9000) + 1000; // 1000–9999
        int expirySecondsInt = 0;

        System.out.println(">>>>>>>>>>>>>" + expirySeconds);

        try {
            expirySecondsInt = Integer.parseInt(expirySeconds);
        } catch (NumberFormatException e) {
            System.out.println("Không phải số hợp lệ!");
        }
        Instant expiryTime = Instant.now().plusSeconds(expirySecondsInt);
        Long secondsLeft = Duration.between(Instant.now(), expiryTime).getSeconds(); // thời gian sống của otp

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setCode(String.valueOf(number));
        otp.setExpiryTime(expiryTime);
        otp.setSecondsLeft(secondsLeft);

        return this.otpRepository.save(otp);
    }

    public boolean verify_otp(Otp reqOtp) {
        Otp otp = this.otpRepository.findById(reqOtp.getEmail()).orElse(null);

        if (otp == null)
            return false;
        if (Instant.now().isAfter(otp.getExpiryTime()))
            return false;
        if (!otp.getCode().equals(reqOtp.getCode()))
            return false;

        return true;
    }

    public void verifyOtpOrThrow(Otp reqOtp) {
        if (!verify_otp(reqOtp)) {
            throw new com.Booking_care.util.error.IdInvalidException("Otp không hợp lệ");
        }
    }

    public void invalidateOtp(String email) {
        this.otpRepository.deleteById(email);
    }

}
