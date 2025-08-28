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
    private Specialty specialty;

    // Clinic
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    private Instant createAt;
    private Instant updateAt;

    public ClinicSpecialty(long id, Specialty specialty, Clinic clinic) {
        this.id = id;
        this.specialty = specialty;
        this.clinic = clinic;
    }

    public ClinicSpecialty() {
    }

    @PrePersist
    public void handleBeforeCreate() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Instant getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Instant createAt) {
        this.createAt = createAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Instant updateAt) {
        this.updateAt = updateAt;
    }

}
