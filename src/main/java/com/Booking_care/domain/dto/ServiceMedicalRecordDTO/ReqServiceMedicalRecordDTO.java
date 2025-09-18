package com.Booking_care.domain.dto.ServiceMedicalRecordDTO;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqServiceMedicalRecordDTO {
    @NotNull(message = "MedicalRecordId không được để trống")
    private Long medicalRecordId;

    @NotNull(message = "ServiceId không được để trống")
    private List<Long> serviceIds;

}
