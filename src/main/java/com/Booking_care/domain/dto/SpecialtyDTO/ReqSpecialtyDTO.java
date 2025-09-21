package com.Booking_care.domain.dto.SpecialtyDTO;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqSpecialtyDTO {
    @NotBlank(message = "name không được để trống")
    private String name;
    private String description;
    private Boolean isActive = true;
    private MultipartFile file;

}
