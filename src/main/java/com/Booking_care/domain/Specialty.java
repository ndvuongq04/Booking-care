package com.Booking_care.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "specialties")
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "name không được để trống")
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;
    private String image; // link file ảnh
    private Boolean isActive = true;

    private Instant createAt;
    private Instant updateAt;

    // Doctor
    @OneToMany(mappedBy = "specialty")
    @JsonIgnore
    private List<Doctor> doctors;

    // ClinicSpecialty
    @OneToMany(mappedBy = "specialty")
    @JsonIgnore
    private List<ClinicSpecialty> clinicSpecialties;

    // MedicalRecord
    @OneToMany(mappedBy = "specialty")
    @JsonIgnore
    private List<MedicalRecord> medicalRecords;

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
