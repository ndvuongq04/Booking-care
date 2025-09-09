package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsByAccountId(long id);

}
