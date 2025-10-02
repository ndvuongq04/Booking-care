package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.dto.PatientDTO.PatientCriteriaDTO;
import com.Booking_care.domain.dto.PatientDTO.ReqPatientDTO;
import com.Booking_care.domain.dto.PatientDTO.ResPatientDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.PatientRepository;
import com.Booking_care.service.specification.PatientSpecs;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final AccountService accountService;

    public PatientService(PatientRepository patientRepository,
            AccountService accountService) {
        this.patientRepository = patientRepository;
        this.accountService = accountService;
    }

    public Patient handleCreatePatient(ReqPatientDTO reqPatient) {
        Patient p = new Patient();
        p.setAccount(this.accountService.fetchAccountById(reqPatient.getAccountId()));
        p.setBhyt(reqPatient.getBhyt());
        return this.patientRepository.save(p);
    }

    public ResPatientDTO convertToResPatientDTO(Patient patient) {
        if (patient == null)
            return null;

        ResPatientDTO resPatientDTO = new ResPatientDTO();

        resPatientDTO.setId(patient.getId());
        resPatientDTO.setBhyt(patient.getBhyt());
        resPatientDTO.setIsActive(patient.getIsActive());

        resPatientDTO.setAccount(this.accountService.convertToResAccountDTO(patient.getAccount()));

        return resPatientDTO;

    }

    public boolean isAccountExits(long id) {
        return this.patientRepository.existsByAccountId(id);
    }

    public ResultPaginationDTO fetchAllPatients(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Patient> page = this.patientRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResPatientDTO> listPatients = page.getContent().stream()
                .map(item -> this.convertToResPatientDTO(item))
                .collect(Collectors.toList());

        res.setResult(listPatients);
        res.setMeta(meta);

        return res;
    }

    public Patient fetchPatientById(long id) {
        Optional<Patient> patient = this.patientRepository.findById(id);

        if (patient.isPresent()) {
            return patient.get();
        }
        return null;
    }

    public Patient handleUpdatePatient(ReqPatientDTO reqPatient, long id) {
        Patient patient = this.fetchPatientById(id);
        if (patient != null) {
            patient.setBhyt(reqPatient.getBhyt());
            this.patientRepository.save(patient);
        }

        return patient;
    }

    @Transactional
    public void handleDeletePatient(long id) {
        Patient patient = this.fetchPatientById(id);
        if (patient != null) {
            patient.setIsActive(false);
            this.patientRepository.save(patient);
        }
    }

    public Page<Patient> getPatientWithSpecs(Pageable pageable, PatientCriteriaDTO patientCriteriaDTO) {
        Specification<Patient> combinedSpec = Specification.where(null);

        // address
        if (patientCriteriaDTO.getAddress() != null && !patientCriteriaDTO.getAddress().trim().isEmpty()) {
            Specification<Patient> currentSpec = PatientSpecs
                    .addressJoinLikeIgnoreCase(patientCriteriaDTO.getAddress());
            combinedSpec = combinedSpec.and(currentSpec);
        }
        // gender
        // name
        if (patientCriteriaDTO.getName() != null && !patientCriteriaDTO.getName().trim().isEmpty()) {
            Specification<Patient> currentSpec = PatientSpecs
                    .nameJoinLikeIgnoreCase(patientCriteriaDTO.getName());
            combinedSpec = combinedSpec.and(currentSpec);
        }
        // phoneNumber
        if (patientCriteriaDTO.getPhoneNumber() != null && !patientCriteriaDTO.getPhoneNumber().trim().isEmpty()) {
            Specification<Patient> currentSpec = PatientSpecs
                    .phoneNumberJoinLikeIgnoreCase(patientCriteriaDTO.getPhoneNumber());
            combinedSpec = combinedSpec.and(currentSpec);
        }
        // bhyt
        if (patientCriteriaDTO.getBhyt() != null && !patientCriteriaDTO.getBhyt().trim().isEmpty()) {
            Specification<Patient> currentSpec = PatientSpecs
                    .bhytLikeIgnoreCase(patientCriteriaDTO.getBhyt());
            combinedSpec = combinedSpec.and(currentSpec);
        }
        // cccd
        if (patientCriteriaDTO.getCccd() != null && !patientCriteriaDTO.getCccd().trim().isEmpty()) {
            Specification<Patient> currentSpec = PatientSpecs
                    .cccdJoinLikeIgnoreCase(patientCriteriaDTO.getCccd());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.patientRepository.findAll(combinedSpec, pageable);

    }

    public ResultPaginationDTO fetchAllPatientsSearch(Pageable pageable, PatientCriteriaDTO patientCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Patient> page = this.getPatientWithSpecs(pageable, patientCriteriaDTO);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResPatientDTO> listPatients = page.getContent().stream()
                .map(item -> this.convertToResPatientDTO(item))
                .collect(Collectors.toList());

        res.setResult(listPatients);
        res.setMeta(meta);

        return res;
    }

}
