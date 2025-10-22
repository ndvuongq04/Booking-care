package com.Booking_care.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Booking_care.domain.BillDetail;

public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {
    List<BillDetail> findAllByBillId(long id);
}
