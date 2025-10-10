package com.Booking_care.domain.dto.ClinicSpecialtyDTO;

import java.util.List;
import com.Booking_care.domain.dto.SpecialtyDTO.ResSpecialtyDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResClinicSpecialtyForClinicDTO {
    private Long clinicId;
    private String clinicName;

    private List<ResSpecialtyDTO> specialties;
}
