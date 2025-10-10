package com.Booking_care.domain.dto.ClinicSpecialtyDTO;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqClinicSpecialtyDTO {

    @NotNull(message = "clinicId không được để trống")
    private Long clinicId;

    @Size(min = 1, message = "Danh sách specialties không được để trống")
    private List<Long> specialties;
}
