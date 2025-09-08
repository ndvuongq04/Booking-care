package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByAccountId(long id);
}