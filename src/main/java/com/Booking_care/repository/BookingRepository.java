package com.Booking_care.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Booking_care.domain.Booking;
import com.Booking_care.domain.enums.BookingStatusEnum;

public interface BookingRepository extends JpaRepository<Booking, Long> {
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

}
