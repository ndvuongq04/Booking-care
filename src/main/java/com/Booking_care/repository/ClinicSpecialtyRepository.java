package com.Booking_care.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.ClinicSpecialty;
import com.Booking_care.domain.Specialty;

public interface ClinicSpecialtyRepository extends JpaRepository<ClinicSpecialty, Long> {
    boolean existsByClinicAndSpecialty(Clinic c, Specialty s);

    boolean existsByClinicAndSpecialtyAndIdNot(Clinic c, Specialty s, Long id);

    Page<ClinicSpecialty> findByClinic_Id(Long id, Pageable pageable);

    Page<ClinicSpecialty> findBySpecialty_Id(Long id, Pageable pageable);

    void deleteByClinic_IdAndSpecialty_Id(Long clinicId, Long specialtyId);

}
