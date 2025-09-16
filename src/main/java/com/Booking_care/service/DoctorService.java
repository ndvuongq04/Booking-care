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
import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;
import com.Booking_care.domain.dto.DoctorDTO.UpdateDoctorDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.DoctorRepository;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final AccountService accountService;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;

    public DoctorService(DoctorRepository doctorRepository,
            AccountService accountService,
            ClinicService clinicService,
            SpecialtyService specialtyService) {
        this.doctorRepository = doctorRepository;
        this.clinicService = clinicService;
        this.accountService = accountService;
        this.specialtyService = specialtyService;
    }

    public boolean isAccountExits(long id) {
        return this.doctorRepository.existsByAccountId(id);
    }

    public Account fetchAccountById(long id) {
        return this.accountService.fetchAccountById(id);
    }

    public Doctor handleCreateDoctor(Doctor doctor) {
        return this.doctorRepository.save(doctor);
    }

    public ResDoctorDTO convertToDoctorDTO(Doctor doctor) {
        if (doctor == null)
            return null;

        ResDoctorDTO dto = new ResDoctorDTO();
        dto.setId(doctor.getId());
        dto.setDegree(doctor.getDegree() != null ? doctor.getDegree().name() : null);
        dto.setIsActive(doctor.getIsActive());
        dto.setCreateAt(doctor.getCreateAt());
        dto.setUpdateAt(doctor.getUpdateAt());

        if (doctor.getAccount() != null) {
            dto.setAccount(this.accountService.convertToResAccountDTO(doctor.getAccount()));
        }

        if (doctor.getClinic() != null) {
            dto.setClinic(this.clinicService.convertToClinicDTO(doctor.getClinic()));
        }

        if (doctor.getSpecialty() != null) {
            dto.setSpecialtyName(doctor.getSpecialty().getName());
            dto.setSpecialtyDescription(doctor.getSpecialty().getDescription());
        }

        return dto;
    }

    public Doctor fetchDoctorById(long id) {
        Optional<Doctor> doc = this.doctorRepository.findById(id);
        if (doc.isPresent()) {
            return doc.get();
        }
        return null;
    }

    public Doctor handleUpdateDoctor(UpdateDoctorDTO doctor) {
        Doctor currentDoctor = this.fetchDoctorById(doctor.getId());
        if (currentDoctor != null) {
            currentDoctor.setCost(doctor.getCost());
            currentDoctor.setDegree(doctor.getDegree());

            if (doctor.getClinic() != null) {
                Clinic clinic = this.clinicService.fetchClinicById(doctor.getClinic().getId());
                currentDoctor.setClinic(clinic != null ? clinic : null);
            }

            if (doctor.getSpecialty() != null) {
                Specialty specialty = this.specialtyService.fetchSpecialtyById(doctor.getSpecialty().getId());
                currentDoctor.setSpecialty(specialty != null ? specialty : null);
            }

            currentDoctor = this.doctorRepository.save(currentDoctor);

        }
        return currentDoctor;
    }

    public void handleDeleteDoctor(Doctor d) {
        d.setIsActive(false);
        this.doctorRepository.save(d);
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
                .map(item -> this.convertToDoctorDTO(item))
                .collect(Collectors.toList());

        res.setResult(listDoc);
        res.setMeta(meta);

        return res;
    }

    public boolean existsByAccountAndIdNot(Account a, long id) {
        Account account = this.fetchAccountById(a.getId());
        return this.doctorRepository.existsByAccountAndIdNot(account, id);
    }

}
