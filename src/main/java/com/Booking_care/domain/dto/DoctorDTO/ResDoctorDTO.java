package com.Booking_care.domain.dto.DoctorDTO;

import java.math.BigDecimal;
import java.time.Instant;

import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import com.Booking_care.domain.dto.SpecialtyDTO.ResSpecialtyDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResDoctorDTO {
    private Long id;
    private String degree;
    private Boolean isActive;
    private Instant createAt;
    private Instant updateAt;
    private BigDecimal cost;
    private String description;
    private ResAccountDTO account;
    private ResClinicDTO clinic;
    private ResSpecialtyDTO specialty;
}
