package com.Booking_care.domain;

import java.math.BigDecimal;
import java.time.Instant;

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
@Table(name = "bill_details")
public class BillDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalService; // thành tiền = serviceCost * quantity
    private BigDecimal serviceCost; // giá 1 dịch vụ
    private Integer quantity; // số lượng
    private Instant createAt;
    private Instant updateAt;

    // Bill
    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;

    // Service
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Services service;

    public BillDetail(Long id, BigDecimal totalService, BigDecimal serviceCost, Integer quantity, Instant createAt,
            Instant updateAt, Bill bill, Services service) {
        this.id = id;
        this.totalService = totalService;
        this.serviceCost = serviceCost;
        this.quantity = quantity;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.bill = bill;
        this.service = service;
    }

    public BillDetail() {
    }

    @PrePersist
    public void handleBeforeCreate() {

        if (this.serviceCost != null && this.quantity != null) {
            this.totalService = this.serviceCost.multiply(BigDecimal.valueOf(this.quantity));
        }

        this.createAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updateAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalService() {
        return totalService;
    }

    public void setTotalService(BigDecimal totalService) {
        this.totalService = totalService;
    }

    public BigDecimal getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(BigDecimal serviceCost) {
        this.serviceCost = serviceCost;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public Services getService() {
        return service;
    }

    public void setService(Services service) {
        this.service = service;
    }

}
