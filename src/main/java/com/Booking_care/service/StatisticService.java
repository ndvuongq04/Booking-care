package com.Booking_care.service;

import com.Booking_care.domain.dto.StatisticDTO.StatisticDTO;
import com.Booking_care.domain.dto.StatisticDTO.StatisticPointDTO;
import com.Booking_care.domain.dto.StatisticDTO.StatisticSummaryDTO;
import com.Booking_care.repository.StatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final StatisticRepository repo;

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public StatisticDTO revenueDaily(LocalDate start, LocalDate end, String status) {
        // [start 00:00:00, end 23:59:59.999999999]
        LocalDateTime s = start.atStartOfDay();
        LocalDateTime e = end.plusDays(1).atStartOfDay().minusNanos(1);

        var points = repo.revenueDaily(s, e, status)
                .stream().map(r -> new StatisticPointDTO(r.getLabel(), nz(r.getTotal()))).toList();

        var sum = repo.revenueSummary(s, e, status);
        var summary = new StatisticSummaryDTO(
                nz(sum.getTotal()),
                sum.getCount() == null ? 0L : sum.getCount(),
                nz(sum.getAvgOrderValue()));
        return new StatisticDTO(points, summary);
    }

    public StatisticDTO revenueMonthly(int year, String status) {
        var points = repo.revenueMonthly(year, status)
                .stream().map(r -> new StatisticPointDTO(r.getLabel(), nz(r.getTotal()))).toList();

        // summary cho cả năm
        LocalDateTime s = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime e = LocalDate.of(year, 12, 31).plusDays(1).atStartOfDay().minusNanos(1);
        var sum = repo.revenueSummary(s, e, status);
        var summary = new StatisticSummaryDTO(
                nz(sum.getTotal()),
                sum.getCount() == null ? 0L : sum.getCount(),
                nz(sum.getAvgOrderValue()));
        return new StatisticDTO(points, summary);
    }

    public StatisticDTO revenueYearly(int startYear, int endYear, String status) {
        var points = repo.revenueYearly(startYear, endYear, status)
                .stream().map(r -> new StatisticPointDTO(r.getLabel(), nz(r.getTotal()))).toList();

        // summary cho giai đoạn
        LocalDateTime s = LocalDate.of(startYear, 1, 1).atStartOfDay();
        LocalDateTime e = LocalDate.of(endYear, 12, 31).plusDays(1).atStartOfDay().minusNanos(1);
        var sum = repo.revenueSummary(s, e, status);
        var summary = new StatisticSummaryDTO(
                nz(sum.getTotal()),
                sum.getCount() == null ? 0L : sum.getCount(),
                nz(sum.getAvgOrderValue()));
        return new StatisticDTO(points, summary);
    }
}
