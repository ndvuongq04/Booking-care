package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Booking_care.domain.Otp;

public interface OtpRepository extends JpaRepository<Otp, String> {

}
