package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Booking_care.domain.Time;

public interface TimeRepository extends JpaRepository<Time, Long> {
}
