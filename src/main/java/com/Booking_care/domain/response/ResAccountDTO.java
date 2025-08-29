package com.Booking_care.domain.response;

import java.time.Instant;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResAccountDTO {
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

    private RoleAccount role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class RoleAccount {
        private long id;
        private String name;
    }
}
