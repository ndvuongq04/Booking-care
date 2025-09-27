package com.Booking_care.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.dto.PatientDTO.ReqPatientDTO;
import com.Booking_care.domain.dto.PatientDTO.ResPatientDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.AccountProfile;
import com.Booking_care.service.AccountService;
import com.Booking_care.service.PatientService;
import com.Booking_care.util.SecurityUtil;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PatientController {
    private final PatientService patientService;
    private final AccountService accountService;
    private final AccountProfile accountProfile;

    public PatientController(PatientService patientService, AccountService accountService,
            AccountProfile accountProfile) {
        this.patientService = patientService;
        this.accountService = accountService;
        this.accountProfile = accountProfile;
    }

    @PostMapping("/patients")
    @ApiMessage("Create new patient")
    public ResponseEntity<ResPatientDTO> createNewPatient(@Valid @RequestBody ReqPatientDTO reqPatient)
            throws IdInvalidException {

        Account acc = this.accountService.fetchAccountById(reqPatient.getAccountId());
        if (acc == null) {
            throw new IdInvalidException("Account không tồn tại");
        }

        this.accountProfile.accountUsed(reqPatient.getAccountId());
        if (this.patientService.isAccountExits(acc.getId())) {
            throw new IdInvalidException("Account đã tồn tại tài khoản bệnh nhân");
        }

        Patient patient = this.patientService.handleCreatePatient(reqPatient);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.patientService.convertToResPatientDTO(patient));
    }

    @GetMapping("/patients")
    @ApiMessage("Fetch all patient")
    public ResponseEntity<ResultPaginationDTO> getAllPatients(
            Pageable pageable) {
        ResultPaginationDTO result = this.patientService.fetchAllPatients(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/patients/{id}")
    @ApiMessage("Fetch patient by id")
    public ResponseEntity<ResPatientDTO> getPatientById(@PathVariable("id") long id) throws IdInvalidException {
        Patient patient = this.patientService.fetchPatientById(id);

        if (patient == null) {
            throw new IdInvalidException("Patient với id " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.patientService.convertToResPatientDTO(patient));
    }

    @PutMapping("/patients/{id}")
    @ApiMessage("Update a patient")
    public ResponseEntity<ResPatientDTO> updatePatient(@PathVariable("id") long id,
            @Valid @RequestBody ReqPatientDTO reqPatient)
            throws IdInvalidException {
        Patient patient = this.patientService.handleUpdatePatient(reqPatient, id);

        if (patient == null) {
            throw new IdInvalidException("Patient với id " + id + " không tồn tại");
        }

        return ResponseEntity.ok(this.patientService.convertToResPatientDTO(patient));
    }

    @DeleteMapping("patients/{id}")
    @ApiMessage("Delete a patient")
    public ResponseEntity<Void> deletePatientById(@PathVariable("id") long id)
            throws IdInvalidException {
        Patient patient = this.patientService.fetchPatientById(id);

        if (patient == null) {
            throw new IdInvalidException("Patient với id " + id + " không tồn tại");
        }
        this.patientService.handleDeletePatient(patient.getId());

        return ResponseEntity.ok(null);
    }

}
