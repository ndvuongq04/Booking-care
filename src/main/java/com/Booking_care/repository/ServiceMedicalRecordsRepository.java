package com.Booking_care.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.ServiceMedicalRecord;

public interface ServiceMedicalRecordsRepository extends JpaRepository<ServiceMedicalRecord, Long> {
    List<ServiceMedicalRecord> findByMedicalRecordId(Long medicalRecordId);
}
