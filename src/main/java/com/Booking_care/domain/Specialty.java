package com.Booking_care.domain;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "specialties")
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private String image; // link file ảnh
    private Instant createAt;
    private Instant updateAt;

    // Doctor
    @OneToMany(mappedBy = "specialty")
    private List<Doctor> doctors;

    // ClinicSpecialty
    @OneToMany(mappedBy = "specialty")
    private List<ClinicSpecialty> clinicSpecialties;

    // MedicalRecord
    @OneToMany(mappedBy = "specialty")
    private List<MedicalRecord> medicalRecords;

    @PrePersist
    public void handleBeforeCreate() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }

    public Specialty() {
    }

    public Specialty(long id, String name, String description, String image, Instant createAt, Instant updateAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public List<ClinicSpecialty> getClinicSpecialties() {
        return clinicSpecialties;
    }

    public void setClinicSpecialties(List<ClinicSpecialty> clinicSpecialties) {
        this.clinicSpecialties = clinicSpecialties;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

}
