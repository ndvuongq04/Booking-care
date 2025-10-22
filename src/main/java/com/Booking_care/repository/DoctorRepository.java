package com.Booking_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    boolean existsByAccountId(long id);

    boolean existsByAccountAndIdNot(Account a, Long id);

    Page<Doctor> findAll(Specification<Doctor> spec, Pageable pageable);

}
