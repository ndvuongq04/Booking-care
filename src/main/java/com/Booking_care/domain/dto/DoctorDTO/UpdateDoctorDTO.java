package com.Booking_care.domain.dto.DoctorDTO;

import java.math.BigDecimal;

import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.enums.DegreeEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDoctorDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Cost không được để trống")
    @DecimalMin(value = "0", inclusive = false, message = "Cost phải lớn hơn 0")
    private BigDecimal cost;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Degree không được để trống")
    private DegreeEnum degree;

    @NotNull(message = "Clinic không được để trống")
    private Clinic clinic;

    @NotNull(message = "Specialty không được để trống")
    private Specialty specialty;

    private String description;
    private Boolean isActive;

}
