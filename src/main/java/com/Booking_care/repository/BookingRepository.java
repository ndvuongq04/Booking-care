package com.Booking_care.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.Booking_care.domain.Booking;
import com.Booking_care.domain.enums.BookingStatusEnum;

public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
        boolean existsByPatientIdAndAppointmentDateAndTimeIdAndStatusNot(
                        Long patientId,
                        LocalDate appointmentDate,
                        Long timeId,
                        BookingStatusEnum status);

        boolean existsByDoctorIdAndAppointmentDateAndTimeIdAndStatusNot(
                        Long doctorId,
                        LocalDate appointmentDate,
                        Long timeId,
                        BookingStatusEnum status);

        Page<Booking> findByPatientId(Long patientId, Pageable pageable);

        Page<Booking> findByDoctorId(Long doctorId, Pageable pageable);

        Page<Booking> findByClinicId(Long clinicId, Pageable pageable);

        Page<Booking> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate, Pageable pageable);

        List<Booking> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

        Page<Booking> findAll(Specification<Booking> specs, Pageable pageable);
}
