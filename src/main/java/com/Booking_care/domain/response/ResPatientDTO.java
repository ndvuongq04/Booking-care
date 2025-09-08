package com.Booking_care.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResPatientDTO {
    private long id;
    private String bhyt;
    private ResAccountDTO account;
}
