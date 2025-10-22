package com.Booking_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    boolean existsByAccountId(long id);

    Page<Patient> findAll(Specification<Patient> spec, Pageable pageable);
}