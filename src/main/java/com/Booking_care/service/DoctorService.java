package com.Booking_care.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Booking_care.controller.*;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.dto.DoctorDTO.DoctorCriteriaDTO;
import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;
import com.Booking_care.domain.dto.DoctorDTO.UpdateDoctorDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.DoctorRepository;
import com.Booking_care.service.specification.DoctorSpecs;

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
        dto.setDescription(doctor.getDescription());
        dto.setCost((doctor.getCost()));

        if (doctor.getAccount() != null) {
            dto.setAccount(this.accountService.convertToResAccountDTO(doctor.getAccount()));
        }

        if (doctor.getClinic() != null) {
            dto.setClinic(this.clinicService.convertToClinicDTO(doctor.getClinic()));
        }

        if (doctor.getSpecialty() != null) {
            dto.setSpecialtyDTO(this.specialtyService.convertToResDTO(doctor.getSpecialty()));
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
            currentDoctor.setDescription(doctor.getDescription());
            currentDoctor.setIsActive(doctor.getIsActive());

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

    public Page<Doctor> getAllWithSpec(DoctorCriteriaDTO doctorCriteriaDTO, Pageable pageable) {
        Specification<Doctor> combinedSpec = Specification.where(null);

        if (doctorCriteriaDTO.getDegree() != null && !doctorCriteriaDTO.getDegree().trim().isEmpty()) {
            Specification<Doctor> currentSpec = DoctorSpecs.degreeEqual(doctorCriteriaDTO.getDegree());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getMonthYear() != null) {
            YearMonth monthYear = doctorCriteriaDTO.getMonthYear();

            Instant from = monthYear.atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng
                    .toInstant();

            Instant to = monthYear.plusMonths(1).atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng kế tiếp
                    .toInstant();

            Specification<Doctor> currentSpec = DoctorSpecs.dateBetween(from, to);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getClinicId() != null) {
            Specification<Doctor> currentSpec = DoctorSpecs.clinicJointEqual(doctorCriteriaDTO.getClinicId());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getSpecialtyId() != null) {
            Specification<Doctor> currentSpec = DoctorSpecs.specialtyJointEqual(doctorCriteriaDTO.getSpecialtyId());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getName() != null && !doctorCriteriaDTO.getName().trim().isEmpty()) {
            Specification<Doctor> currentSpec = DoctorSpecs.nameJoinLikeIgnoreCase(doctorCriteriaDTO.getName());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getCost() != null) {
            Specification<Doctor> currentSpec = DoctorSpecs.costBetween(doctorCriteriaDTO.getCost().getMin(),
                    doctorCriteriaDTO.getCost().getMax());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (doctorCriteriaDTO.getPhoneNumber() != null && !doctorCriteriaDTO.getPhoneNumber().trim().isEmpty()) {
            Specification<Doctor> currentSpec = DoctorSpecs.phoneNumberJointLike(doctorCriteriaDTO.getPhoneNumber());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.doctorRepository.findAll(combinedSpec, pageable);

    }

    public ResultPaginationDTO getDoctorSearch(DoctorCriteriaDTO doctorCriteriaDTO, Pageable pageable) {
        Page<Doctor> listPage = this.getAllWithSpec(doctorCriteriaDTO, pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(listPage.getTotalPages());
        meta.setTotals(listPage.getTotalElements());

        // convert
        List<ResDoctorDTO> listDoc = listPage.getContent().stream()
                .map(item -> this.convertToDoctorDTO(item))
                .collect(Collectors.toList());

        res.setResult(listDoc);
        res.setMeta(meta);

        return res;
    }

}
