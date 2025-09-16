package com.Booking_care.domain.dto.AuthDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqLoginDTO {
    @NotBlank(message = "username không được để trống")
    private String userName; // email

    @NotBlank(message = "password không được để trống")
    private String password;

}
