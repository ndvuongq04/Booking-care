package com.Booking_care.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.Booking_care.domain.enums.DegreeEnum;

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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doctors")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Cost không được để trống")
    @DecimalMin(value = "0", inclusive = false, message = "Cost phải lớn hơn 0")
    private BigDecimal cost;

    private Instant createAt;
    private Instant updateAt;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Degree không được để trống")
    private DegreeEnum degree;
    private Boolean isActive = true;

    // Account
    @OneToOne
    @JoinColumn(name = "account_id")
    @NotNull(message = "Account không được để trống")
    private Account account;

    // Clinic
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    @NotNull(message = "Clinic không được để trống")
    private Clinic clinic;

    // Specialty
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    @NotNull(message = "Specialty không được để trống")
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
        if (isActive == null) {
            isActive = true;
        }

        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }
}
