package com.Booking_care.domain.dto.DoctorDTO;

import java.math.BigDecimal;
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
public class DoctorCriteriaDTO {
    private String degree;
    private Long specialtyId;
    private Long clinicId;

    @DateTimeFormat(pattern = "MM/yyyy")
    private YearMonth monthYear;

    private String name;
    private String phoneNumber;

    private CostRange cost;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CostRange {
        private Integer min;
        private Integer max;
    }
}
