package com.Booking_care.domain.dto.SupportDTO;

import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;

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
    private ResClinicDTO clinic;
}
