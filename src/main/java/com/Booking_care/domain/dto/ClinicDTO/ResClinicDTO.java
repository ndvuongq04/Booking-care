package com.Booking_care.domain.dto.ClinicDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResClinicDTO {
    private Long id;
    private String name;
    private String description;
    private String position;
    private String phoneNumber;
    private String image;
    private ResAddressDTO address;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResAddressDTO {
        private long id;
        private String city;
    }
}
