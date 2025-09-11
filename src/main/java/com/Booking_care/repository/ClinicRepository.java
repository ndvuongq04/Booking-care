package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Clinic;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

}
