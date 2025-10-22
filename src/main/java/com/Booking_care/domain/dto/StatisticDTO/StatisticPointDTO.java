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
public class StatisticPointDTO {
    private String label; // ngày/tháng/năm
    private BigDecimal total; // tổng doanh thu/ số lượng bill
}
