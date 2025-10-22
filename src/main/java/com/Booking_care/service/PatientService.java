package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.dto.PatientDTO.PatientCriteriaDTO;
import com.Booking_care.domain.dto.PatientDTO.ReqPatientDTO;
import com.Booking_care.domain.dto.PatientDTO.ResPatientDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.PatientRepository;
import com.Booking_care.service.specification.PatientSpecs;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.util.error.BusinessException;
import com.Booking_care.mapper.patient.PatientMapper;

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
        // Validation 1: Check account exists (will throw if not found)
        Account account = this.accountService.fetchAccountById(reqPatient.getAccountId());
        
        // Validation 2: Check account's role is CLIENT (FIX BUG - tương tự Doctor module)
        if (account.getRole() == null || !"CLIENT".equals(account.getRole().getName())) {
            throw new BusinessException("Chỉ tài khoản với role CLIENT mới có thể tạo patient profile");
        }
        
        // Validation 3: Check account chưa có patient profile
        if (this.patientRepository.existsByAccountId(account.getId())) {
            throw new BusinessException("Tài khoản này đã có patient profile");
        }

        try {
            Patient p = new Patient();
            p.setAccount(account);
            p.setBhyt(reqPatient.getBhyt());
            return this.patientRepository.save(p);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo patient: " + e.getMessage());
        }
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
                .map(PatientMapper::toResPatientDTO)
                .collect(Collectors.toList());

        res.setResult(listPatients);
        res.setMeta(meta);

        return res;
    }

    public Patient fetchPatientById(long id) {
        return this.patientRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Patient với id " + id + " không tồn tại"));
    }

    public Patient handleUpdatePatient(ReqPatientDTO reqPatient, long id) {
        // Validation: check patient exists (will throw if not found)
        Patient patient = this.fetchPatientById(id);

        try {
            patient.setBhyt(reqPatient.getBhyt());
            return this.patientRepository.save(patient);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật patient: " + e.getMessage());
        }
    }

    @Transactional
    public void handleDeletePatient(long id) {
        // Validation: check patient exists (will throw if not found)
        Patient patient = this.fetchPatientById(id);

        try {
            patient.setIsActive(false);
            this.patientRepository.save(patient);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa patient: " + e.getMessage());
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
                .map(PatientMapper::toResPatientDTO)
                .collect(Collectors.toList());

        res.setResult(listPatients);
        res.setMeta(meta);

        return res;
    }

}
