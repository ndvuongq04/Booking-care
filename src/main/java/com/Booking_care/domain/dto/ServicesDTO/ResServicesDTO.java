package com.Booking_care.domain.dto.ServicesDTO;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResServicesDTO {
    private long id;
    private String name;
    private BigDecimal cost;
    private String description;
    private Instant createAt;
    private Instant updateAt;

}
