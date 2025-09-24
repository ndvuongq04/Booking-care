package com.Booking_care.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Setter
@Getter
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "otp")
public class Otp {
    @Id
    private String email;
    private String code;
    private Instant expiryTime;
    private Instant currentSubmit;
    private Long secondsLeft;

    // account temp
    private String password;
    private String name;
}