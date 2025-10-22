package com.Booking_care.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clinic_specialty"
// , uniqueConstraints = @UniqueConstraint(name = "uk_clinic_specialty",
// columnNames = {
// "clinic_id", "specialty_id" })
)
public class ClinicSpecialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Specialty
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    @NotNull(message = "Specialty không được để trống")
    private Specialty specialty;

    // Clinic
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    @NotNull(message = "Clinic không được để trống")
    private Clinic clinic;

    private Instant createAt;
    private Instant updateAt;

    @PrePersist
    public void handleBeforeCreate() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }

}
