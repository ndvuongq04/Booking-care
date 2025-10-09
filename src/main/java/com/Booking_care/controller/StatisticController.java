package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Booking_care.service.StatisticService;

@RestController
@RequestMapping("/api/v1")
public class StatisticController {
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    // thống kê tổng tiền
    // thống kê lịch khám đã hoàn thành
    // thống kê hóa đơn
}
