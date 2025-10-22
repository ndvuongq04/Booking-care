package com.Booking_care.domain.dto.StatisticDTO;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticSummaryDTO {
    private BigDecimal total; // tổng price/ bill
    private Long count; // tổng price/ bill
    private BigDecimal avgOrderValue;
}
