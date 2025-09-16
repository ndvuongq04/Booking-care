package com.Booking_care.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResFeedbackDTO {
    private Long id;
    private String description;
    private ResDoctorDTO doctor;

}
