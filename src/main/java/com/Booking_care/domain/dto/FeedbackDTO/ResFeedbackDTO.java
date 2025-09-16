package com.Booking_care.domain.dto.FeedbackDTO;

import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;

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
