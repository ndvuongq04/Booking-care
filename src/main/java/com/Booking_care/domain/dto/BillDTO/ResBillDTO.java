package com.Booking_care.domain.dto.BillDTO;

import java.util.List;

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
@NoArgsConstructor
@AllArgsConstructor
public class ResBillDTO {
    private PatientDTO patient;
    private ResMedicalRecordDTO medicalRecord;
    private ResSupportDTO support;
    private BillStatusEnum status;
    private List<ServiceItemDTO> services;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceItemDTO {
        private ResServicesDTO service;
        private Integer quantity;
    }

}
