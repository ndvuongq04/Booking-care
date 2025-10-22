package com.Booking_care.domain.dto.AccountDTO;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.Booking_care.domain.enums.GenderEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccountDTO {
    @NotNull(message = "Id không được null")
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private String address;
    private GenderEnum gender;
    private String cccd;
    private LocalDate birth;
    private Long roleId;

    private MultipartFile file;

}
