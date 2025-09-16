package com.Booking_care.domain.response;

import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.Clinic;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
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
}
