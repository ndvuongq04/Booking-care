package com.Booking_care.domain.dto.ClinicSpecialtyDTO;

import java.util.List;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResClinicSpecialtyForSpecialtyDTO {
    private Long specialtyId;
    private String specialtyName;

    private List<ResClinicDTO> specialties;
}
