package com.Booking_care.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;
import com.Booking_care.domain.dto.PatientDTO.ResPatientDTO;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResFeedbackDTO {
    private Long id;
    private String description;
    private int rate;
    private ResDoctorDTO doctor;
    private ResPatientDTO patient;

}
