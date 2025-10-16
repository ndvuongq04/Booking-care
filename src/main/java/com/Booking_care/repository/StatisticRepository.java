package com.Booking_care.repository;

import com.Booking_care.domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Bill, Long> {

    // ProjectionsS
    public interface StatisticRow {
        String getLabel(); // cột nhóm (ngày/tháng/năm)

        BigDecimal getTotal(); // tổng tiền
    }

    public interface StatisticSummaryRow {
        BigDecimal getTotal(); // tổng tiền

        Long getCount(); // số bill

        BigDecimal getAvgOrderValue(); // TB mỗi bill
    }

    @Query(value = """
                SELECT DATE(b.create_at) AS label,
                       COALESCE(SUM(b.total_bill), 0) AS total
                FROM bills b
                WHERE (:status IS NULL OR b.status = :status)
                  AND b.create_at BETWEEN :start AND :end
                GROUP BY DATE(b.create_at)
                ORDER BY DATE(b.create_at)
            """, nativeQuery = true)
    List<StatisticRow> revenueDaily(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") String status);

    @Query(value = """
                SELECT DATE_FORMAT(b.create_at, '%Y-%m') AS label,
                       COALESCE(SUM(b.total_bill), 0) AS total
                FROM bills b
                WHERE (:status IS NULL OR b.status = :status)
                  AND YEAR(b.create_at) = :year
                GROUP BY DATE_FORMAT(b.create_at, '%Y-%m')
                ORDER BY DATE_FORMAT(b.create_at, '%Y-%m')
            """, nativeQuery = true)
    List<StatisticRow> revenueMonthly(
            @Param("year") int year,
            @Param("status") String status);

    @Query(value = """
                SELECT YEAR(b.create_at) AS label,
                       COALESCE(SUM(b.total_bill), 0) AS total
                FROM bills b
                WHERE (:status IS NULL OR b.status = :status)
                  AND YEAR(b.create_at) BETWEEN :startYear AND :endYear
                GROUP BY YEAR(b.create_at)
                ORDER BY YEAR(b.create_at)
            """, nativeQuery = true)
    List<StatisticRow> revenueYearly(
            @Param("startYear") int startYear,
            @Param("endYear") int endYear,
            @Param("status") String status);

    @Query(value = """
              SELECT
                  COALESCE(SUM(b.total_bill), 0)                                   AS total,
                  COUNT(b.total_bill)                                              AS count,
                  COALESCE(SUM(b.total_bill) / NULLIF(COUNT(b.total_bill), 0), 0)  AS avgOrderValue
              FROM bills b
              WHERE (:status IS NULL OR b.status = :status)
                AND b.create_at BETWEEN :start AND :end
            """, nativeQuery = true)
    StatisticSummaryRow revenueSummary(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") String status);

}
