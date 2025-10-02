package com.Booking_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Support;

public interface SupportRepository extends JpaRepository<Support, Long> {

    boolean existsByAccountId(long id);

    Page<Support> findAll(Specification<Support> specs, Pageable pageable);
}
