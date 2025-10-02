package com.Booking_care.domain.dto.SpecialtyDTO;

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
public class SpecialtyCriteriaDTO {
    private String name;

    @DateTimeFormat(pattern = "MM/yyyy")
    private YearMonth monthYear;
}
