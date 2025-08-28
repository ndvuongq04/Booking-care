package com.Booking_care.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.persistence.Entity;
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

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private BigDecimal cost;
    private Instant createAt;
    private Instant updateAt;

    // Account
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // Degree
    @ManyToOne
    @JoinColumn(name = "degree_id")
    private Degree degree;

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

    public Doctor() {
    }

    public Doctor(long id, BigDecimal cost, Instant createAt, Instant updateAt, Account account,
            Degree degree, Clinic clinic, Specialty specialty) {
        this.id = id;
        this.cost = cost;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.account = account;
        this.degree = degree;
        this.clinic = clinic;
        this.specialty = specialty;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Degree getDegree() {
        return degree;
    }

    public void setDegree(Degree degree) {
        this.degree = degree;
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

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

}
