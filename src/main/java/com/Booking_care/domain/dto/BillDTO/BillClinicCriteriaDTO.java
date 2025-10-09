package com.Booking_care.domain.dto.BillDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillClinicCriteriaDTO {
    private Long clinicId;
    private String phoneNumber;
    private String cccd;
    private String email;
}
