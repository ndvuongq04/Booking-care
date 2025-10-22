package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.domain.dto.StatisticDTO.StatisticDTO;
import com.Booking_care.service.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import com.Booking_care.util.error.IdInvalidException;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1")
public class StatisticController {
    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    // PRICE
    // DAILY
    @GetMapping("statistic/price/daily")
    @ApiMessage("Revenue daily")
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticDTO revenueDaily(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(required = false) String status) throws IdInvalidException {
        return statisticService.revenueDaily(start, end, status);
    }

    // MONTHLY
    @GetMapping("statistic/price/monthly")
    @ApiMessage("Revenue monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticDTO revenueMonthly(
            @RequestParam int year,
            @RequestParam(required = false) String status) throws IdInvalidException {
        return statisticService.revenueMonthly(year, status);
    }

    // YEARLY
    @GetMapping("statistic/price/yearly")
    @ApiMessage("Revenue yearly")
    @PreAuthorize("hasRole('ADMIN')")
    public StatisticDTO revenueYearly(
            @RequestParam int startYear,
            @RequestParam int endYear,
            @RequestParam(required = false) String status) throws IdInvalidException {
        return statisticService.revenueYearly(startYear, endYear, status);
    }

    // BOOKING SUCCESS
    // DAILY
    @GetMapping("statistic/bookingSuccess/daily")
    @ApiMessage("bookingSuccess daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'SUPPORT')")
    public StatisticDTO bookingSuccessDaily(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            @RequestParam(required = false, defaultValue = "COMPLETED") String status,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long clinicId) throws IdInvalidException {
        return statisticService.successDaily(start, end, status, doctorId, clinicId);
    }

    // MONTHLY
    @GetMapping("statistic/bookingSuccess/monthly")
    @ApiMessage("bookingSuccess monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'SUPPORT')")
    public StatisticDTO bookingSuccessMonthly(
            @RequestParam int year,
            @RequestParam(required = false, defaultValue = "COMPLETED") String status,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long clinicId) throws IdInvalidException {
        return statisticService.successMonthly(year, status, doctorId, clinicId);
    }

    // YEARLY
    @GetMapping("statistic/bookingSuccess/yearly")
    @ApiMessage("bookingSuccess yearly")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'SUPPORT')")
    public StatisticDTO bookingSuccessYearly(
            @RequestParam int startYear,
            @RequestParam int endYear,
            @RequestParam(required = false, defaultValue = "COMPLETED") String status,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long clinicId) throws IdInvalidException {
        return statisticService.successYearly(startYear, endYear, status, doctorId, clinicId);
    }

}
