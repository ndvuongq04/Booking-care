package com.Booking_care.domain.dto.AuthDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResLoginDTO {
    private String accessToken;
    private UserLogin userLogin;

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {
        private long id;
        private String name;
        private String email;
        private String role;
        private Long patientId;

    }
}
