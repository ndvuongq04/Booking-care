package com.Booking_care.domain.dto.StatisticDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticDTO {
    private List<StatisticPointDTO> points; // danh sách mốc (ngày/tháng/năm) và doanh thu
    private StatisticSummaryDTO summary; // tổng quan: tổng doanh thu, số đơn, AOV
}
