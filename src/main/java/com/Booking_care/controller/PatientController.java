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
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.dto.PatientDTO.PatientCriteriaDTO;
import com.Booking_care.domain.dto.PatientDTO.ReqPatientDTO;
import com.Booking_care.domain.dto.PatientDTO.ResPatientDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.PatientService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.patient.PatientMapper;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;

    }

    @PostMapping("/patients")
    @ApiMessage("Create new patient")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResPatientDTO> createNewPatient(@Valid @RequestBody ReqPatientDTO reqPatient)
            throws IdInvalidException {
        Patient patient = this.patientService.handleCreatePatient(reqPatient);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PatientMapper.toResPatientDTO(patient));
    }

    @GetMapping("/patients")
    @ApiMessage("Fetch all patient")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllPatients(
            Pageable pageable) {
        ResultPaginationDTO result = this.patientService.fetchAllPatients(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/patients/{id}")
    @ApiMessage("Fetch patient by id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResPatientDTO> getPatientById(@PathVariable("id") long id) throws IdInvalidException {
        Patient patient = this.patientService.fetchPatientById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(PatientMapper.toResPatientDTO(patient));
    }

    @PutMapping("/patients/{id}")
    @ApiMessage("Update a patient")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResPatientDTO> updatePatient(@PathVariable("id") long id,
            @Valid @RequestBody ReqPatientDTO reqPatient)
            throws IdInvalidException {
        Patient patient = this.patientService.handleUpdatePatient(reqPatient, id);
        return ResponseEntity.ok(PatientMapper.toResPatientDTO(patient));
    }

    @DeleteMapping("patients/{id}")
    @ApiMessage("Delete a patient")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePatientById(@PathVariable("id") long id)
            throws IdInvalidException {
        this.patientService.handleDeletePatient(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/patients/search")
    @ApiMessage("Fetch all patient search/fiter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllPatientsSearch(
            Pageable pageable, PatientCriteriaDTO patientCriteriaDTO) {
        ResultPaginationDTO result = this.patientService.fetchAllPatientsSearch(pageable, patientCriteriaDTO);
        return ResponseEntity.ok().body(result);
    }

}
