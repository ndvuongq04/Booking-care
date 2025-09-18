package com.Booking_care.domain.dto.MedicalRecordDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqMedicalRecordDTO {
    private long id;

    @NotBlank(message = "Mô tả bệnh án không được để trống")
    private String description;

    @NotNull(message = "PatientId không được để trống")
    private Long patientId;

    @NotNull(message = "DoctorId không được để trống")
    private Long doctorId;

    @NotNull(message = "ClinicId không được để trống")
    private Long clinicId;

    @NotNull(message = "SpecialtyId không được để trống")
    private Long specialtyId;
}
