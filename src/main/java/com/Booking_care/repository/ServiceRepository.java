package com.Booking_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.Booking_care.domain.Services;

public interface ServiceRepository extends JpaRepository<Services, Long>, JpaSpecificationExecutor<Services> {
    boolean existsByName(String name);

    Page<Services> findAll(Specification<Services> specs, Pageable pageable);

}
