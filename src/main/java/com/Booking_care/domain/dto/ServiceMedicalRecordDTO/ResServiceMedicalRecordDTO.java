package com.Booking_care.domain.dto.ServiceMedicalRecordDTO;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResServiceMedicalRecordDTO {
    private Long id;
    private Instant createAt;
    private Instant updateAt;

    private ServiceDTO service;

    @Getter
    @Setter
    public static class ServiceDTO {
        private Long id;
        private String name;
        private BigDecimal cost;
        private String description;
    }
}
