package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Booking_care.domain.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {

}
