package com.Booking_care.domain.dto.BillDTO;

import java.util.List;
import com.Booking_care.domain.enums.BillStatusEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqBillDTO {
    @NotNull(message = "PatientId không được để trống")
    private Long patientId;

    @NotNull(message = "MedicalRecordId không được để trống")
    private Long medicalRecordId;

    @NotNull(message = "SupportId không được để trống")
    private Long supportId;
    private BillStatusEnum status;

    @NotEmpty(message = "Services không được để trống")
    private List<ServiceItemDTO> services;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceItemDTO {
        private Long serviceId;
        private Integer quantity;
    }

}
