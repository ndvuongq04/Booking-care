package com.Booking_care.domain;

import java.time.Instant;

import com.Booking_care.domain.enums.BookingStatus;

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
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;
    private Instant createAt;
    private Instant updateAt;
    private BookingStatus status; // dùng enum thay vì String

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

    public Booking(long id, String description, Instant createAt, Instant updateAt, BookingStatus status) {
        this.id = id;
        this.description = description;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.status = status;
    }

    public Booking() {
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

}
