package com.Booking_care.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResFeedbackDTO {
    private Long id;
    private String description;
    private ResDoctorDTO doctor;

}
