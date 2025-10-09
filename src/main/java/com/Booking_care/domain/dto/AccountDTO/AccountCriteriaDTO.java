package com.Booking_care.domain.dto.AccountDTO;

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
public class AccountCriteriaDTO {
    private String roleName;
    private String gender;

    @DateTimeFormat(pattern = "MM/yyyy")
    private YearMonth monthYear;

    private String cccd;
    private String email;
    private String phoneNumber;

}
