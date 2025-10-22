package com.Booking_care.domain.dto.BillDTO;

import java.util.List;
import com.Booking_care.domain.dto.BillDetailDTO.ReqBillDetailDTO;
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
    private Long id;

    @NotNull(message = "PatientId không được để trống")
    private Long patientId;

    private Long medicalRecordId;

    @NotNull(message = "SupportId không được để trống")
    private Long supportId;

    @NotEmpty(message = "Services không được để trống")
    private List<ReqBillDetailDTO> services;

}
