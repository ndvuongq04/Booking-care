package com.Booking_care.domain;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "clinics")
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private String position; // vị trí (có thể để toạ độ hoặc text)
    private String phoneNumber;
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

    public Clinic() {
    }

    public Clinic(long id, String name, String description, String position, String phoneNumber, Instant createAt,
            Instant updateAt, String image, Address address) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.position = position;
        this.phoneNumber = phoneNumber;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.image = image;
        this.address = address;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public List<Support> getSupports() {
        return supports;
    }

    public void setSupports(List<Support> supports) {
        this.supports = supports;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

}
