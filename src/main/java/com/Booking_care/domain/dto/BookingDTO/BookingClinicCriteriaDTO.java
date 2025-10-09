package com.Booking_care.domain.dto.BookingDTO;

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
public class BookingClinicCriteriaDTO {
    private Long clinicId;
    private String doctorName;
    private String patientName;
    @DateTimeFormat(pattern = "MM/yyyy")
    private YearMonth monthYear;
}
