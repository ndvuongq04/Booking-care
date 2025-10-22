package com.Booking_care.domain.dto.ClinicDTO;

import java.time.YearMonth;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicCriteriaDTO {
    private Long addressId;
    private String name;

    private String phoneNumber;

    @DateTimeFormat(pattern = "MM/yyyy")
    private YearMonth monthYear;
}
