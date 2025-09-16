package com.Booking_care.domain.dto.SpecialtyDTO;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResSpecialtyDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Boolean isActive;
    private Instant createAt;
    private Instant updateAt;
}
