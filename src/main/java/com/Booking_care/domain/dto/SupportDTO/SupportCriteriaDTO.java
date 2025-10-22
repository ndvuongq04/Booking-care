package com.Booking_care.domain.dto.SupportDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SupportCriteriaDTO {
    private String address;
    private Long clinicId;
    private String name;
    private String phoneNumber;
}
