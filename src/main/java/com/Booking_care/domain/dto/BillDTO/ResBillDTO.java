package com.Booking_care.domain.dto.BillDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.Booking_care.domain.dto.BillDetailDTO.ResBillDetailDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO.PatientDTO;
import com.Booking_care.domain.dto.ServicesDTO.ResServicesDTO;
import com.Booking_care.domain.dto.SupportDTO.ResSupportDTO;
import com.Booking_care.domain.enums.BillStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResBillDTO {
    private Long id;

    // Quan hệ
    private PatientDTO patient;
    private MedicalRecordDTO medicalRecord;
    private SupportDTO support;

    private List<ResBillDetailDTO> services;

    // Thông tin hóa đơn
    private BigDecimal totalBill;
    private BillStatusEnum status;

    private Instant createAt;
    private Instant updateAt;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PatientDTO {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MedicalRecordDTO {
        private Long id;
        private String description;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SupportDTO {
        private Long id;
        private String name;
    }
}
