package com.Booking_care.domain.dto.BookingDTO;

import java.time.Instant;
import java.time.LocalDate;

import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;
import com.Booking_care.domain.dto.PatientDTO.ResPatientDTO;
import com.Booking_care.domain.enums.BookingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResBookingDTO {
    private Long id;
    private LocalDate appointmentDate;
    private String description;
    private Instant createAt;
    private Instant updateAt;
    private BookingStatusEnum status;
    private ResDoctorDTO doctor;
    private ResPatientDTO patient;
    private ResClinicDTO clinic;
    private ResTimeDTO time;
    private Boolean checkFeedback;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResTimeDTO {
        private long id;
        private String start;
        private String end;
    }
}