package com.Booking_care.domain.dto.BillDTO;

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
public class BillCriteriaDTO {
    private Long serviceId;
    @DateTimeFormat(pattern = "MM/yyyy")
    private YearMonth monthYear;

    private String accountName;
    private Long billId;

}
