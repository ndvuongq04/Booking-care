package com.Booking_care.domain.response;

import java.time.Instant;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateAccountDTO {
    private long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String gender;
    private String address;
    private LocalDate birth;
    private Instant createAt;
    private Instant updateAt;
    private String cccd;
}
