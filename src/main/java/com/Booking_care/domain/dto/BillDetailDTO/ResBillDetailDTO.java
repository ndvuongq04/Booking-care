package com.Booking_care.domain.dto.BillDetailDTO;

import java.math.BigDecimal;
import java.time.Instant;

import com.Booking_care.domain.dto.ServicesDTO.ResServicesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResBillDetailDTO {
    private Long id;
    private ResServicesDTO service;
    private Integer quantity;
    private BigDecimal serviceCost;
    private BigDecimal totalService;
    private Instant createAt;
    private Instant updateAt;
}
