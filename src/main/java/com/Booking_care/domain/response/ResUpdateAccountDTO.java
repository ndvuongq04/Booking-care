package com.Booking_care.domain.response;

import java.time.Instant;
import java.time.LocalDate;

import com.Booking_care.domain.enums.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateAccountDTO {
    private long id;
    private String name;
    private String phoneNumber;
    private GenderEnum gender;
    private String address;
    private LocalDate birth;
    private Instant updateAt;
    private String cccd;
    private RoleAccount roleAccount;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleAccount {
        private long id;
        private String name;
    }
}
