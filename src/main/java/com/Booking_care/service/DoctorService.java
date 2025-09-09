package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.response.ResAccountDTO;
import com.Booking_care.domain.response.ResDoctorDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.AccountRepository;
import com.Booking_care.repository.DoctorRepository;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public DoctorService(DoctorRepository doctorRepository,
            AccountRepository accountRepository,
            AccountService accountService) {
        this.doctorRepository = doctorRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    public boolean isAccountExits(long id) {
        return this.doctorRepository.existsByAccountId(id);
    }

    public Account fetchAccountById(long id) {
        Optional<Account> acc = this.accountRepository.findById(id);
        if (acc.isPresent()) {
            return acc.get();
        }
        return null;
    }

    public Doctor handleCreateDoctor(Doctor doctor) {
        return this.doctorRepository.save(doctor);
    }

    public ResDoctorDTO convertToResDoctorDTO(Doctor doctor) {
        ResDoctorDTO res = new ResDoctorDTO();

        res.setId(doctor.getId());
        res.setCost(doctor.getCost());
        res.setCreateAt(doctor.getCreateAt());
        res.setUpdateAt(doctor.getUpdateAt());
        res.setDegree(doctor.getDegree());
        res.setAccount(this.accountService.convertToResAccountDTO(doctor.getAccount()));
        // thiếu call clinic và specialty qua id -> lấy thông tin -> gán vào dto
        res.setClinic(doctor.getClinic());
        res.setSpecialty(doctor.getSpecialty());

        return res;
    }

    public Doctor fetchDoctorById(long id) {
        Optional<Doctor> doc = this.doctorRepository.findById(id);
        if (doc.isPresent()) {
            return doc.get();
        }
        return null;
    }

    public Doctor handleUpdateDoctor(Doctor doctor) {
        Doctor currentDoctor = this.fetchDoctorById(doctor.getId());
        if (currentDoctor != null) {
            currentDoctor.setCost(doctor.getCost());
            currentDoctor.setDegree(doctor.getDegree());

            if (doctor.getClinic() != null) {
                // call api clinic , kta id của clinic có ok ko
                Clinic clinic = new Clinic();
                // set value
                currentDoctor.setClinic(clinic != null ? clinic : null);
            }

            if (doctor.getSpecialty() != null) {
                // call api Specialty , kta id của Specialty có ok ko
                Specialty specialty = new Specialty();
                // set value
                currentDoctor.setSpecialty(specialty != null ? specialty : null);
            }

            currentDoctor = this.doctorRepository.save(currentDoctor);

        }
        return currentDoctor;
    }

    public void handleDeleteDoctor(long id) {
        this.doctorRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllDoctor(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Doctor> page = this.doctorRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResDoctorDTO> listDoc = page.getContent().stream()
                .map(item -> this.convertToResDoctorDTO(item))
                .collect(Collectors.toList());

        res.setResult(listDoc);
        res.setMeta(meta);

        return res;
    }

}
