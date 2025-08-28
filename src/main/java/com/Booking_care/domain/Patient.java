package com.Booking_care.domain;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String bhyt; // Bảo hiểm y tế

    // Account
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account; // giữ khóa ngoại

    // MedicalRecord
    @OneToMany(mappedBy = "patient")
    private List<MedicalRecord> medicalRecords;

    // Booking
    @OneToMany(mappedBy = "patient")
    private List<Booking> bookings;

    // Bill
    @OneToMany(mappedBy = "patient")
    private List<Bill> bills;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBhyt() {
        return bhyt;
    }

    public void setBhyt(String bhyt) {
        this.bhyt = bhyt;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public Patient() {
    }

    public Patient(long id, String bhyt, Account account) {
        this.id = id;
        this.bhyt = bhyt;
        this.account = account;
    }

}
