package com.Booking_care.service;

import org.springframework.stereotype.Service;
import com.Booking_care.domain.enums.RoleName;
import com.Booking_care.repository.AccountRepository;
import com.Booking_care.repository.DoctorRepository;
import com.Booking_care.repository.PatientRepository;
import com.Booking_care.repository.SupportRepository;
import com.Booking_care.util.error.IdInvalidException;

@Service
public class AccountProfile {
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SupportRepository supportRepository;
    private final AccountRepository accountRepository;

    public AccountProfile(DoctorRepository doctorRepository, PatientRepository patientRepository,
            SupportRepository supportRepository, AccountRepository accountRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.supportRepository = supportRepository;
        this.accountRepository = accountRepository;
    }

    public void accountUsed(long accountId) throws IdInvalidException {
        if (this.doctorRepository.existsByAccountId(accountId)) {
            throw new IdInvalidException("Account đã được sử dụng cho hồ sơ khác");
        }

        if (this.patientRepository.existsByAccountId(accountId)) {
            throw new IdInvalidException("Account đã được sử dụng cho hồ sơ khác");
        }

        if (this.supportRepository.existsByAccountId(accountId)) {
            throw new IdInvalidException("Account đã được sử dụng cho hồ sơ khác");
        }
    }

    public boolean accountHasRole(long accountId, RoleName role) {
        if (role == null)
            return false;
        return accountRepository.existsByIdAndRole_NameIgnoreCase(accountId, role.name());
    }

}
