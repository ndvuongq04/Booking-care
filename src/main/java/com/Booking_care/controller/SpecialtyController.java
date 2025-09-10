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

import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.SpecialtyService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SpecialtyController {
    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @PostMapping("/specialties")
    @ApiMessage("Create new specialty")
    public ResponseEntity<Specialty> createNewSpecialty(
            @Valid @RequestBody Specialty reqSpecialty) throws IdInvalidException {

        boolean isNameExits = this.specialtyService.isNameExits(reqSpecialty.getName());
        if (isNameExits) {
            throw new IdInvalidException(
                    "Tên chuyên khoa '" + reqSpecialty.getName() + "' đã tồn tại, vui lòng chọn tên khác");
        }
        Specialty s = this.specialtyService.handleCreateSpecialty(reqSpecialty);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(s);
    }

    @GetMapping("/specialties/{id}")
    @ApiMessage("Fetch specialty by id")
    public ResponseEntity<Specialty> getSpecialtyById(@PathVariable("id") Long id)
            throws IdInvalidException {
        Specialty s = this.specialtyService.fetchSpecialtyById(id);

        if (s == null) {
            throw new IdInvalidException("Specialty với id " + id + " không tồn tại");
        }

        return ResponseEntity.ok(s);
    }

    @PutMapping("/specialties")
    @ApiMessage("Update a specialty")
    public ResponseEntity<Specialty> updateSpecialty(
            @Valid @RequestBody Specialty reqSpecialty)
            throws IdInvalidException {

        Specialty current = this.specialtyService.fetchSpecialtyById(reqSpecialty.getId());
        if (current == null) {
            throw new IdInvalidException("Specialty với id " + reqSpecialty.getId() + " không tồn tại");
        }

        boolean nameExisted = this.specialtyService.existsByNameAndIdNot(reqSpecialty.getName(), reqSpecialty.getId());
        if (nameExisted) {
            throw new IdInvalidException(
                    "Tên chuyên khoa '" + reqSpecialty.getName() + "' đã tồn tại, vui lòng chọn tên khác");
        }

        Specialty s = this.specialtyService.handleUpdateSpecialty(reqSpecialty);

        return ResponseEntity.ok(s);
    }

    @DeleteMapping("/specialties/{id}")
    @ApiMessage("Delete a specialty") // set isActive = false
    public ResponseEntity<Void> deleteSpecialty(@PathVariable("id") Long id)
            throws IdInvalidException {
        Specialty s = this.specialtyService.fetchSpecialtyById(id);

        if (s == null) {
            throw new IdInvalidException("Specialty với id " + id + " không tồn tại");
        }

        this.specialtyService.handleDeleteSpecialty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/specialties")
    @ApiMessage("Fetch all specialties")
    public ResponseEntity<ResultPaginationDTO> getAllSpecialties(Pageable pageable) {
        ResultPaginationDTO result = this.specialtyService.fetchAllSpecialty(pageable);

        return ResponseEntity.ok(result);
    }

}
