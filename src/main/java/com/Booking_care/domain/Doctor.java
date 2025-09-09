package com.Booking_care.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.Booking_care.domain.enums.DegreeEnum;

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

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private BigDecimal cost;
    private Instant createAt;
    private Instant updateAt;
    @Enumerated(EnumType.STRING)
    private DegreeEnum degree;

    // Account
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // Clinic
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    // Specialty
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    // Feedback
    @OneToMany(mappedBy = "doctor")
    private List<Feedback> feedbacks;

    // MedicalRecord
    @OneToMany(mappedBy = "doctor")
    private List<MedicalRecord> medicalRecords;

    // Booking
    @OneToMany(mappedBy = "doctor")
    private List<Booking> bookings;

    @PrePersist
    public void handleBeforeCreate() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }

}
