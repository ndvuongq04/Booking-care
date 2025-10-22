package com.Booking_care.domain.dto;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResCloudinaryDTO {
    private String publicId;
    private String url;
}
