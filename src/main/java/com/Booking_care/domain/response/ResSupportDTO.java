package com.Booking_care.domain.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.Booking_care.domain.Bill;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.enums.DegreeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResSupportDTO {
    private long id;
    private Boolean isActive;
    private ResAccountDTO account;
    private Clinic clinic;
    private List<Bill> bill;
}
