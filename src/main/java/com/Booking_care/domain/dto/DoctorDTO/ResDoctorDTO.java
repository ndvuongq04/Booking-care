package com.Booking_care.domain.dto.DoctorDTO;

import java.time.Instant;

import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResDoctorDTO {
    private Long id;
    private String degree;
    private Boolean isActive;
    private Instant createAt;
    private Instant updateAt;
    private ResAccountDTO account;
    private ResClinicDTO clinic;
    private String specialtyName;
    private String specialtyDescription;
}
