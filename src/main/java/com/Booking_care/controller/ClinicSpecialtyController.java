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
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.ClinicSpecialty;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.request.accountDTO.CreateAccountDTO;
import com.Booking_care.domain.request.accountDTO.UpdateAccountDTO;
import com.Booking_care.domain.response.ResAccountDTO;
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
    @ApiMessage("Create new clinicSpecialty")
    public ResponseEntity<ClinicSpecialty> createNewClinicSpecialty(@Valid @RequestBody ClinicSpecialty req)
            throws IdInvalidException {

        this.checkException(req.getClinic(), req.getSpecialty());
        boolean isClinicSpecialtyExits = this.clinicSpecialtyService.isClinicSpecialtyExits(req.getClinic(),
                req.getSpecialty());
        if (isClinicSpecialtyExits) {
            throw new IdInvalidException("Clinic đã tồn tại specialty này ");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.clinicSpecialtyService.handleCreateClinicSpecialty(req));
    }

    @GetMapping("/clinicSpecialties/{id}")
    @ApiMessage("Fetch clinicSpecialty by id")
    public ResponseEntity<ClinicSpecialty> getClinicSpecialtyByClinicId(@PathVariable("id") long id)
            throws IdInvalidException {
        ClinicSpecialty cS = this.clinicSpecialtyService.fetchClinicSpecialtyById(id);

        if (cS == null) {
            throw new IdInvalidException("ClinicSpecialty với id " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(cS);
    }

    @DeleteMapping("/clinicSpecialties/{id}")
    @ApiMessage("Delete clinicSpecialty by id")
    public ResponseEntity<Void> deleteClinicSpecialtyByClinicId(@PathVariable("id") long id)
            throws IdInvalidException {
        ClinicSpecialty cS = this.clinicSpecialtyService.fetchClinicSpecialtyById(id);

        if (cS == null) {
            throw new IdInvalidException("ClinicSpecialty với id " + id + " không tồn tại");
        }

        this.clinicSpecialtyService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @PutMapping("/clinicSpecialties")
    @ApiMessage("Update a clinicSpecialty")
    public ResponseEntity<ClinicSpecialty> updateClinicSpecialty(@Valid @RequestBody ClinicSpecialty req)
            throws IdInvalidException {

        ClinicSpecialty cS = this.clinicSpecialtyService.fetchClinicSpecialtyById(req.getId());
        if (cS == null) {
            throw new IdInvalidException("ClinicSpecialty với id " + req.getId() + " không tồn tại");
        }
        if (this.clinicSpecialtyService.existsByClinicAndSpecialty(req.getClinic(), req.getSpecialty(), req.getId())) {
            throw new IdInvalidException("Clinic và Specialty này đã tồn tại");
        }
        this.checkException(req.getClinic(), req.getSpecialty());

        return ResponseEntity.ok(this.clinicSpecialtyService.handleUpdateClinicSpecialty(req));
    }

    @GetMapping("/clinicSpecialties")
    @ApiMessage("Fetch all clinicSpecialty")
    public ResponseEntity<ResultPaginationDTO> getAllClinicSpecialty(
            Pageable pageable) {
        ResultPaginationDTO result = this.clinicSpecialtyService.fetchAllClinicSpecialty(pageable);
        return ResponseEntity.ok().body(result);
    }

    private void checkException(Clinic c, Specialty s) throws IdInvalidException {
        boolean isClinicExits = this.clinicSpecialtyService.isClinicExits(c.getId());
        if (!isClinicExits) {
            throw new IdInvalidException("Clinic với id : " + c.getId() + " không tồn tại");
        }

        boolean isSpecialtyExits = this.clinicSpecialtyService.isSpecialtyExits(s.getId());
        if (!isSpecialtyExits) {
            throw new IdInvalidException("Specialty với id : " + s.getId() + " không tồn tại");
        }
    }

}
