package com.Booking_care.domain.dto.PatientDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqPatientDTO {
    private Long AccountId;
    private String bhyt;
}
