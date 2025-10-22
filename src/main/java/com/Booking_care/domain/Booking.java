package com.Booking_care.domain;

import java.time.Instant;
import java.time.LocalDate;

import com.Booking_care.domain.enums.BookingStatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate appointmentDate; // ngày khám
    private String description;
    private Instant createAt;
    private Instant updateAt;
    private Boolean checkFeedback = false;

    @Enumerated(EnumType.STRING)
    private BookingStatusEnum status;

    // Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // Patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Clinic
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    // Time
    @ManyToOne
    @JoinColumn(name = "time_id")
    private Time time;

    // Feedback
    @OneToOne(mappedBy = "booking")
    private Feedback feedback;

    @PrePersist
    public void handleBeforeCreate() {
        if (status == null) {
            status = BookingStatusEnum.PENDING;
        }

        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }
}
