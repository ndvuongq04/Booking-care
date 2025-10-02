package com.Booking_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Booking_care.domain.MedicalRecord;

public interface MedicalRecordsRepository extends JpaRepository<MedicalRecord, Long> {
    Page<MedicalRecord> findByDoctorId(Pageable pageable, long doctorId);
}
