package com.Booking_care.domain;

import java.math.BigDecimal;
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
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private BigDecimal cost;
    private String description;
    private Instant createAt;
    private Instant updateAt;

    // ServiceMedicalRecord
    @OneToMany(mappedBy = "service")
    private List<ServiceMedicalRecord> serviceMedicalRecords;

    // BillDetail
    @OneToMany(mappedBy = "service")
    private List<BillDetail> billDetails;

    @PrePersist
    public void handleBeforeCreate() {
        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }

    public Service() {
    }

    public Service(long id, String name, BigDecimal cost, String description, Instant createAt,
            Instant updateAt) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.description = description;
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

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
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

    public List<ServiceMedicalRecord> getServiceMedicalRecords() {
        return serviceMedicalRecords;
    }

    public void setServiceMedicalRecords(List<ServiceMedicalRecord> serviceMedicalRecords) {
        this.serviceMedicalRecords = serviceMedicalRecords;
    }

    public List<BillDetail> getBillDetails() {
        return billDetails;
    }

    public void setBillDetails(List<BillDetail> billDetails) {
        this.billDetails = billDetails;
    }

}
