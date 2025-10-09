package com.Booking_care.domain.dto.FeedbackDTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqFeedbackDTO {
    @Min(1)
    @Max(5)
    private int rate;

    private String description;

    @NotNull
    private Long doctorId;

    @NotNull
    private Long patientId;

    private Long bookingId;

}
