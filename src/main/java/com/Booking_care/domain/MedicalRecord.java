package com.Booking_care.domain;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;
    private Instant createAt;
    private Instant updateAt;

    // Patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // Clinic
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    // Specialty
    @ManyToOne
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    // ServiceMedicalRecord
    @OneToMany(mappedBy = "medicalRecord")
    private List<ServiceMedicalRecord> serviceMedicalRecords;

    // Bill
    @OneToMany(mappedBy = "medicalRecord")
    private List<Bill> bills;

    public MedicalRecord() {
    }

    public MedicalRecord(long id, String description, Instant createAt, Instant updateAt, Patient patient,
            Doctor doctor, Clinic clinic, Specialty specialty) {
        this.id = id;
        this.description = description;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.patient = patient;
        this.doctor = doctor;
        this.clinic = clinic;
        this.specialty = specialty;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public List<ServiceMedicalRecord> getServiceMedicalRecords() {
        return serviceMedicalRecords;
    }

    public void setServiceMedicalRecords(List<ServiceMedicalRecord> serviceMedicalRecords) {
        this.serviceMedicalRecords = serviceMedicalRecords;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    @PrePersist
    public void handleBeforeCreate() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }

}
