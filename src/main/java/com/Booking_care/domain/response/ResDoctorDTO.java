package com.Booking_care.domain.response;

import java.math.BigDecimal;
import java.time.Instant;

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
public class ResDoctorDTO {
    private long id;
    private BigDecimal cost;
    private Instant createAt;
    private Instant updateAt;
    private DegreeEnum degree;
    private ResAccountDTO account;
    private Clinic clinic;
    private Specialty specialty;

}
