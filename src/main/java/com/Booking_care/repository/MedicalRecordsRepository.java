package com.Booking_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.Booking_care.domain.MedicalRecord;

public interface MedicalRecordsRepository
        extends JpaRepository<MedicalRecord, Long>, JpaSpecificationExecutor<MedicalRecord> {
    Page<MedicalRecord> findByDoctorId(Pageable pageable, long doctorId);

    Page<MedicalRecord> findAll(Specification<MedicalRecord> specs, Pageable pageable);

}
