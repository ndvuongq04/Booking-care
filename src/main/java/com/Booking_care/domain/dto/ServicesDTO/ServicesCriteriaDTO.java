package com.Booking_care.domain.dto.ServicesDTO;

import com.Booking_care.domain.dto.DoctorDTO.DoctorCriteriaDTO.CostRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServicesCriteriaDTO {
    private String name;
    private CostRange cost;

}
