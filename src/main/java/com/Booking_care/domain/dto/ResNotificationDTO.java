package com.Booking_care.domain.dto;

import java.time.Instant;

import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResNotificationDTO {
    private Long id;
    private String title;
    private String content;
    private Instant createAt;
    private ResAccountDTO account;

}
