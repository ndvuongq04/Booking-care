package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Services;

public interface ServiceRepository extends JpaRepository<Services, Long> {

}
