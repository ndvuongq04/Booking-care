package com.Booking_care.domain.dto.PatientDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientCriteriaDTO {
    private String address;
    // private String gender;
    private String name;
    private String phoneNumber;
    private String bhyt;
    private String cccd;
}
