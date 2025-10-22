package com.Booking_care.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.dto.ClinicSpecialtyDTO.ReqClinicSpecialtyDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.ClinicSpecialtyService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ClinicSpecialtyController {
    private final ClinicSpecialtyService clinicSpecialtyService;

    public ClinicSpecialtyController(ClinicSpecialtyService clinicSpecialtyService) {
        this.clinicSpecialtyService = clinicSpecialtyService;
    }

    @PostMapping("/clinicSpecialties")
    @ApiMessage("Add list of Specialty for the clinic")
    public ResponseEntity<Void> addListSpecialtyForClinic(@Valid @RequestBody ReqClinicSpecialtyDTO req)
            throws IdInvalidException {
        this.clinicSpecialtyService.handleAddSpecialtiesForClinic(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(null);
    }

    @GetMapping("/clinicSpecialties/clinic/{clinicId}")
    @ApiMessage("Fetch specialties for clinicId")
    public ResponseEntity<ResultPaginationDTO> getClinicSpecialtyByClinicId(@PathVariable("clinicId") long clinicId,
            Pageable pageable)
            throws IdInvalidException {
        return ResponseEntity.ok(this.clinicSpecialtyService.fetchClinicSpecialtyByClinicId(clinicId, pageable));
    }

    @GetMapping("/clinicSpecialties/specialty/{specialtyId}")
    @ApiMessage("Fetch clinics for specialtyId")
    public ResponseEntity<ResultPaginationDTO> getClinicsBySpecialtyId(
            @PathVariable("specialtyId") long specialtyId,
            Pageable pageable)
            throws IdInvalidException {
        return ResponseEntity.ok(this.clinicSpecialtyService.fetchClinicSpecialtyBySpecialtyId(specialtyId, pageable));
    }

    @GetMapping("/clinicSpecialties")
    @ApiMessage("Fetch all clinicSpecialty")
    public ResponseEntity<ResultPaginationDTO> getAllClinicSpecialty(
            Pageable pageable) {
        ResultPaginationDTO result = this.clinicSpecialtyService.fetchAllClinicSpecialty(pageable);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/clinicSpecialties/delete")
    @ApiMessage("Fetch all clinicSpecialty")
    public ResponseEntity<Void> deleteClinicSpecialty(
            Pageable pageable,
            @RequestParam("clinicId") long clinicId,
            @RequestParam("specialtyId") long specialtyId) {
        this.clinicSpecialtyService.handleDeleteClinicSpecialty(clinicId, specialtyId);
        return ResponseEntity.ok(null);
    }

}
