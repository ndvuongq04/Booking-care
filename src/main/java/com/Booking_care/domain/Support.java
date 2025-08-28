package com.Booking_care.domain;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "supports")
public class Support {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Account
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // Clinic
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    // Bill
    @OneToMany(mappedBy = "support")
    private List<Bill> bills;

    public Support(long id, Account account, Clinic clinic) {
        this.id = id;
        this.account = account;
        this.clinic = clinic;
    }

    public Support() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

}
