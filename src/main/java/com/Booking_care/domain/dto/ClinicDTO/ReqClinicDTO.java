package com.Booking_care.domain.dto.ClinicDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReqClinicDTO {
    @NotBlank(message = "Tên không được để trống")
    private String name;

    private String description;

    @NotBlank(message = "Vị trí không được để trống")
    private String position;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotNull(message = "Địa chỉ không được để trống")
    private Long addressId;

    private Boolean isActive;

}
