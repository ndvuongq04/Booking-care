package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.dto.DoctorDTO.DoctorCriteriaDTO;
import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;
import com.Booking_care.domain.dto.DoctorDTO.UpdateDoctorDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.DoctorService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.doctor.DoctorMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("doctors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResDoctorDTO> createNewDoctor(@Valid @RequestBody Doctor doctor) throws IdInvalidException {
        Doctor doctorDB = this.doctorService.handleCreateDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(DoctorMapper.toResDoctorDTO(doctorDB));
    }

    @PutMapping("doctors")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ResDoctorDTO> updateDoctor(@Valid @RequestBody UpdateDoctorDTO reqDoctor)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DoctorMapper.toResDoctorDTO(this.doctorService.handleUpdateDoctor(reqDoctor)));
    }

    @GetMapping("/doctors/{id}")
    @ApiMessage("Fetch doctor by id")
    public ResponseEntity<ResDoctorDTO> getDoctorById(@PathVariable("id") long id) throws IdInvalidException {
        Doctor doctor = this.doctorService.fetchDoctorById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DoctorMapper.toResDoctorDTO(doctor));
    }

    @DeleteMapping("/doctors/{id}")
    @ApiMessage("Delete doctor by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable("id") long id)
            throws IdInvalidException {
        this.doctorService.handleDeleteDoctor(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @GetMapping("/doctors")
    @ApiMessage("Fetch all doctor")
    public ResponseEntity<ResultPaginationDTO> getAllDoctors(
            Pageable pageable) {
        ResultPaginationDTO result = this.doctorService.fetchAllDoctor(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("doctors/search")
    public ResponseEntity<ResultPaginationDTO> searchAndFilter(
            @Valid @ModelAttribute DoctorCriteriaDTO doctorCriteriaDTO,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.doctorService.getDoctorSearch(doctorCriteriaDTO, pageable));

    }
}
