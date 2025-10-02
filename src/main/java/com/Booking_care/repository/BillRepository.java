package com.Booking_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.Booking_care.domain.Bill;

public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {
    Page<Bill> findByPatientId(long patientId, Pageable pageable);

    Page<Bill> findBySupport_Clinic_Id(long clinicId, Pageable pageable);

    Page<Bill> findAll(Specification<Bill> specs, Pageable pageable);

}
