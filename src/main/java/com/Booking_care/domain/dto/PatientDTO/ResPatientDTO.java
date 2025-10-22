package com.Booking_care.domain.dto.PatientDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;

import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResPatientDTO {
    private Long id;
    private String bhyt;
    private Boolean isActive;
    private ResAccountDTO account;
}
