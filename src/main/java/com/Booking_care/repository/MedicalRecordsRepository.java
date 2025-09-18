package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Booking_care.domain.MedicalRecord;

public interface MedicalRecordsRepository extends JpaRepository<MedicalRecord, Long> {

}
