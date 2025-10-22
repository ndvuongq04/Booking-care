package com.Booking_care.domain.dto.AccountDTO;

import java.time.Instant;
import java.time.LocalDate;

import com.Booking_care.domain.enums.GenderEnum;

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
    private GenderEnum gender;
    private String address;
    private LocalDate birth;
    private Instant createAt;
    private Instant updateAt;
    private String cccd;
    private String avatar;

    private RoleAccount role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleAccount {
        private long id;
        private String name;
    }
}
