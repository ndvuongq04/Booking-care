package com.Booking_care.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Booking_care.domain.Support;

public interface SupportRepository extends JpaRepository<Support, Long> {

    boolean existsByAccountId(long id);

    Page<Support> findAll(Specification<Support> specs, Pageable pageable);

    @Query("select s.clinic.id from Support s where s.id = :supportId")
    Optional<Long> findClinicIdBySupportId(@Param("supportId") Long supportId);

}
