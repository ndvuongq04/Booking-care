package com.Booking_care.domain.dto.BookingDTO;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingDTO {
    @NotNull(message = "BookingId không được để trống")
    private Long id;

    @NotNull(message = "Ngày khám không được để trống")
    private LocalDate appointmentDate;

    private String description;

    @NotNull(message = "ClinicId không được để trống")
    private Long clinicId;

    @NotNull(message = "TimeId không được để trống")
    private Long timeId;
}
