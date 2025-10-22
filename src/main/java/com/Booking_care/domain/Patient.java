package com.Booking_care.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String bhyt; // Bảo hiểm y tế

    private Boolean isActive = true;

    // Account
    @OneToOne
    @JoinColumn(name = "account_id", nullable = true)
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

    // Feedback
    @OneToMany(mappedBy = "patient")
    @JsonIgnore
    private List<Feedback> feedbacks;

    @PrePersist
    public void handleBeforeCreate() {
        if (isActive == null) {
            isActive = true;
        }

    }

}
