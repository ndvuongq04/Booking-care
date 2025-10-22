package com.Booking_care.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.dto.ClinicDTO.ClinicCriteriaDTO;
import com.Booking_care.domain.dto.ClinicDTO.ReqClinicDTO;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.ClinicService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.clinic.ClinicMapper;
import com.Booking_care.util.error.StorageException;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ClinicController {
    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PostMapping(value = "/clinics")
    @ApiMessage("Create new clinic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResClinicDTO> createNewClinic(@Valid @RequestBody ReqClinicDTO reqClinic)
            throws IdInvalidException {
        Clinic c = this.clinicService.handleCreateClinic(reqClinic);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ClinicMapper.toResClinicDTO(c));
    }

    @GetMapping("/clinics/{id}")
    @ApiMessage("Fetch clinic by id")
    public ResponseEntity<ResClinicDTO> getClinicById(@PathVariable("id") long id) throws IdInvalidException {
        Clinic c = this.clinicService.fetchClinicById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ClinicMapper.toResClinicDTO(c));
    }

    @DeleteMapping("clinics/{id}")
    @ApiMessage("Delete a clinic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClinicById(@PathVariable("id") long id) throws IdInvalidException {
        this.clinicService.handleDeleteClinic(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping(value = "/clinics/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Update a clinic")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ResClinicDTO> updateAccount(@Valid @ModelAttribute ReqClinicDTO reqClinic,
            @PathVariable("id") long id,
            @RequestParam(value = "file", required = false) MultipartFile file)
            throws IdInvalidException, StorageException {
        Clinic clinic = this.clinicService.handleUpdateClinic(reqClinic, id, file);
        return ResponseEntity.ok(ClinicMapper.toResClinicDTO(clinic));
    }

    @GetMapping("/clinics")
    @ApiMessage("Fetch all clinic")
    public ResponseEntity<ResultPaginationDTO> getAllClinic(
            Pageable pageable) {
        ResultPaginationDTO result = this.clinicService.fetchAllClinic(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/clinics/search")
    @ApiMessage("Fetch all clinic search/filter")
    public ResponseEntity<ResultPaginationDTO> getAllClinicSearch(
            Pageable pageable, ClinicCriteriaDTO clinicCriteriaDTO) {
        ResultPaginationDTO result = this.clinicService.fetchAllClinicSearch(pageable, clinicCriteriaDTO);
        return ResponseEntity.ok().body(result);
    }
}
