package com.Booking_care.service;

import com.Booking_care.domain.dto.StatisticDTO.StatisticDTO;
import com.Booking_care.domain.dto.StatisticDTO.StatisticPointDTO;
import com.Booking_care.domain.dto.StatisticDTO.StatisticSummaryDTO;
import com.Booking_care.repository.StatisticRepository;
import com.Booking_care.util.error.IdInvalidException;
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

    private static long n0(Long v) {
        return v == null ? 0L : v;
    }

    public StatisticDTO revenueDaily(LocalDate start, LocalDate end, String status) {
        // Validation: check date range
        if (start == null || end == null || end.isBefore(start)) {
            throw new IdInvalidException("Tham số ngày không hợp lệ (end phải >= start)");
        }

        try {
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
        } catch (Exception e) {
            throw new IdInvalidException("Không thể lấy thống kê doanh thu theo ngày: " + e.getMessage());
        }
    }

    public StatisticDTO revenueMonthly(int year, String status) {
        // Validation: check year range
        if (year < 2000 || year > 2100) {
            throw new IdInvalidException("Năm không hợp lệ (2000–2100)");
        }

        try {
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
        } catch (IdInvalidException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể lấy thống kê doanh thu theo tháng: " + e.getMessage());
        }
    }

    public StatisticDTO revenueYearly(int startYear, int endYear, String status) {
        // Validation: check year range
        if (endYear < startYear) {
            throw new IdInvalidException("Tham số năm không hợp lệ (endYear phải >= startYear)");
        }
        if (startYear < 2000 || endYear > 2100) {
            throw new IdInvalidException("Khoảng năm không hợp lệ (2000–2100)");
        }

        try {
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
        } catch (IdInvalidException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể lấy thống kê doanh thu theo năm: " + e.getMessage());
        }
    }

    // Booking success

    public StatisticDTO successDaily(LocalDate start, LocalDate end, String status,
            Long doctorId, Long clinicId) {
        // Validation: check date range
        if (start == null || end == null || end.isBefore(start)) {
            throw new IdInvalidException("Tham số ngày không hợp lệ (end phải >= start)");
        }

        try {
            var points = repo.bookingSuccessDaily(start, end, status, doctorId, clinicId)
                    .stream().map(r -> new StatisticPointDTO(r.getLabel(), nz(r.getTotal()))).toList();

            var sum = repo.bookingSuccessSummary(start, end, status, doctorId, clinicId);
            var summary = new StatisticSummaryDTO(
                    nz(sum.getTotal()), // tổng số booking (dạng BigDecimal)
                    n0(sum.getCount()), // số booking
                    BigDecimal.ZERO // avgOrderValue không áp dụng cho count
            );
            return new StatisticDTO(points, summary);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể lấy thống kê booking theo ngày: " + e.getMessage());
        }
    }

    public StatisticDTO successMonthly(int year, String status, Long doctorId, Long clinicId) {
        // Validation: check year range
        if (year < 2000 || year > 2100) {
            throw new IdInvalidException("Năm không hợp lệ (2000–2100)");
        }

        try {
            var points = repo.bookingSuccessMonthly(year, status, doctorId, clinicId)
                    .stream().map(r -> new StatisticPointDTO(r.getLabel(), nz(r.getTotal()))).toList();

            // summary cả năm
            LocalDate s = LocalDate.of(year, 1, 1);
            LocalDate e = LocalDate.of(year, 12, 31);
            var sum = repo.bookingSuccessSummary(s, e, status, doctorId, clinicId);

            var summary = new StatisticSummaryDTO(
                    nz(sum.getTotal()),
                    n0(sum.getCount()),
                    BigDecimal.ZERO);
            return new StatisticDTO(points, summary);
        } catch (IdInvalidException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể lấy thống kê booking theo tháng: " + e.getMessage());
        }
    }

    public StatisticDTO successYearly(int startYear, int endYear, String status,
            Long doctorId, Long clinicId) {
        // Validation: check year range
        if (endYear < startYear) {
            throw new IdInvalidException("Tham số năm không hợp lệ (endYear phải >= startYear)");
        }
        if (startYear < 2000 || endYear > 2100) {
            throw new IdInvalidException("Khoảng năm không hợp lệ (2000–2100)");
        }

        try {
            var points = repo.bookingSuccessYearly(startYear, endYear, status, doctorId, clinicId)
                    .stream().map(r -> new StatisticPointDTO(r.getLabel(), nz(r.getTotal()))).toList();

            // summary giai đoạn
            LocalDate s = LocalDate.of(startYear, 1, 1);
            LocalDate e = LocalDate.of(endYear, 12, 31);
            var sum = repo.bookingSuccessSummary(s, e, status, doctorId, clinicId);

            var summary = new StatisticSummaryDTO(
                    nz(sum.getTotal()),
                    n0(sum.getCount()),
                    BigDecimal.ZERO);
            return new StatisticDTO(points, summary);
        } catch (IdInvalidException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể lấy thống kê booking theo năm: " + e.getMessage());
        }
    }
}
