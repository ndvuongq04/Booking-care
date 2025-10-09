package com.Booking_care.domain;

import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clinics")
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String position; // vị trí (có thể để toạ độ hoặc text)

    private String phoneNumber;
    private Boolean isActive = true;

    private Instant createAt;
    private Instant updateAt;
    private String image; // link hoặc tên file ảnh

    // Doctor
    @OneToMany(mappedBy = "clinic")
    @JsonIgnore
    private List<Doctor> doctors;

    // Support
    @OneToMany(mappedBy = "clinic")
    @JsonIgnore
    private List<Support> supports;

    // Address
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    // ClinicSpecialty
    @OneToMany(mappedBy = "clinic")
    @JsonIgnore
    private List<ClinicSpecialty> clinicSpecialties;

    // MedicalRecord
    @OneToMany(mappedBy = "clinic")
    @JsonIgnore
    private List<MedicalRecord> medicalRecords;

    // Booking
    @OneToMany(mappedBy = "clinic")
    @JsonIgnore
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
