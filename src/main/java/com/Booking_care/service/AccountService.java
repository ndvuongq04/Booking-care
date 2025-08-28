package com.Booking_care.service;

import org.springframework.stereotype.Service;

import com.Booking_care.repository.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

}
