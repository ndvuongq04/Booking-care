package com.Booking_care.repository;

import com.Booking_care.domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    // Booking Success
    @Query(value = """
                SELECT b.appointment_date AS label,
                       CAST(COUNT(*) AS DECIMAL(20,0)) AS total
                FROM bookings b
                WHERE (:status IS NULL OR b.status = :status)
                  AND (:doctorId IS NULL OR b.doctor_id = :doctorId)
                  AND (:clinicId IS NULL OR b.clinic_id = :clinicId)
                  AND b.appointment_date BETWEEN :start AND :end
                GROUP BY b.appointment_date
                ORDER BY b.appointment_date
            """, nativeQuery = true)
    List<StatisticRow> bookingSuccessDaily(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("status") String status,
            @Param("doctorId") Long doctorId,
            @Param("clinicId") Long clinicId);

    @Query(value = """
                SELECT DATE_FORMAT(b.appointment_date, '%Y-%m') AS label,
                       CAST(COUNT(*) AS DECIMAL(20,0)) AS total
                FROM bookings b
                WHERE (:status IS NULL OR b.status = :status)
                  AND (:doctorId IS NULL OR b.doctor_id = :doctorId)
                  AND (:clinicId IS NULL OR b.clinic_id = :clinicId)
                  AND YEAR(b.appointment_date) = :year
                GROUP BY DATE_FORMAT(b.appointment_date, '%Y-%m')
                ORDER BY DATE_FORMAT(b.appointment_date, '%Y-%m')
            """, nativeQuery = true)
    List<StatisticRow> bookingSuccessMonthly(
            @Param("year") int year,
            @Param("status") String status,
            @Param("doctorId") Long doctorId,
            @Param("clinicId") Long clinicId);

    @Query(value = """
                SELECT YEAR(b.appointment_date) AS label,
                       CAST(COUNT(*) AS DECIMAL(20,0)) AS total
                FROM bookings b
                WHERE (:status IS NULL OR b.status = :status)
                  AND (:doctorId IS NULL OR b.doctor_id = :doctorId)
                  AND (:clinicId IS NULL OR b.clinic_id = :clinicId)
                  AND YEAR(b.appointment_date) BETWEEN :startYear AND :endYear
                GROUP BY YEAR(b.appointment_date)
                ORDER BY YEAR(b.appointment_date)
            """, nativeQuery = true)
    List<StatisticRow> bookingSuccessYearly(
            @Param("startYear") int startYear,
            @Param("endYear") int endYear,
            @Param("status") String status,
            @Param("doctorId") Long doctorId,
            @Param("clinicId") Long clinicId);

    @Query(value = """
                SELECT
                    CAST(COUNT(*) AS DECIMAL(20,0)) AS total,
                    COUNT(*)                         AS count,
                    CAST(0 AS DECIMAL(20,2))         AS avgOrderValue
                FROM bookings b
                WHERE (:status IS NULL OR b.status = :status)
                  AND (:doctorId IS NULL OR b.doctor_id = :doctorId)
                  AND (:clinicId IS NULL OR b.clinic_id = :clinicId)
                  AND b.appointment_date BETWEEN :start AND :end
            """, nativeQuery = true)
    StatisticSummaryRow bookingSuccessSummary(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("status") String status,
            @Param("doctorId") Long doctorId,
            @Param("clinicId") Long clinicId);

}
