package com.Booking_care.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Booking_care.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    boolean existsByEmail(String email);

    Page<Account> findAll(Specification<Account> spec, Pageable pageable);

    Account findByEmail(String email);

    Account findByRefreshTokenAndEmail(String token, String email);

    boolean existsByIdAndRole_NameIgnoreCase(Long id, String roleName);

}