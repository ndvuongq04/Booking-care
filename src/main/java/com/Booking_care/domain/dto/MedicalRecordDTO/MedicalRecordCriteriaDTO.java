package com.Booking_care.domain.dto.MedicalRecordDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordCriteriaDTO {
    private Long doctorId;
    private String name;
    private String phoneNumber;
}
