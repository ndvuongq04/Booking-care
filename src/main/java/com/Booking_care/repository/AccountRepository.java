package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}