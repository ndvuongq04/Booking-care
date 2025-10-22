package com.Booking_care.domain;

import java.time.Instant;
import java.util.List;

import com.Booking_care.domain.enums.GenderEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String email;

    private String password;

    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private String address;
    private LocalDate birth;
    private Instant createAt;
    private Instant updateAt;
    private String cccd;

    private String avatar;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    // Role
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    // Notification
    @OneToMany(mappedBy = "account")
    private List<Notification> notifications;

    // Patient
    @OneToOne(mappedBy = "account")
    private Patient patient; // ánh xạ ngược

    // Doctor
    @OneToOne(mappedBy = "account")
    private Doctor doctor;

    // Support
    @OneToOne(mappedBy = "account")
    private Support support;

    @PrePersist
    public void handleBeforeCreate() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }

}
