package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.request.UpdateDoctorDTO;
import com.Booking_care.domain.response.ResDoctorDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.ClinicService;
import com.Booking_care.service.DoctorService;
import com.Booking_care.service.SpecialtyService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class DoctorController {
    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;

    public DoctorController(DoctorService doctorService,
            ClinicService clinicService,
            SpecialtyService specialtyService) {
        this.doctorService = doctorService;
        this.clinicService = clinicService;
        this.specialtyService = specialtyService;
    }

    @PostMapping("doctors")
    public ResponseEntity<ResDoctorDTO> createNewDoctor(@Valid @RequestBody Doctor doctor) throws IdInvalidException {

        if (this.doctorService.fetchAccountById(doctor.getAccount().getId()) == null) {
            throw new IdInvalidException("Account với id " + doctor.getAccount().getId() + " không tồn tại");
        }

        if (this.doctorService.isAccountExits(doctor.getAccount().getId())) {
            throw new IdInvalidException(
                    "Account với id " + doctor.getAccount().getId() + " đã được sử dụng cho một bác sĩ khác");
        }

        if (this.clinicService.fetchClinicById(doctor.getClinic().getId()) == null) {
            throw new IdInvalidException("Clinic với id :" + doctor.getClinic().getId() + " không tồn tại");
        }

        if (this.specialtyService.fetchSpecialtyById(doctor.getSpecialty().getId()) == null) {
            throw new IdInvalidException("Specialty với id :" + doctor.getSpecialty().getId() + " không tồn tại");
        }

        Doctor doctorDB = this.doctorService.handleCreateDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.doctorService.convertToResDoctorDTO(doctorDB));
    }

    @PutMapping("doctors")
    public ResponseEntity<ResDoctorDTO> updateDoctor(@Valid @RequestBody UpdateDoctorDTO reqDoctor)
            throws IdInvalidException {

        if (this.doctorService.fetchDoctorById(reqDoctor.getId()) == null) {
            throw new IdInvalidException("Doctor với id " + reqDoctor.getId() + " không tồn tại");
        }
        if (this.clinicService.fetchClinicById(reqDoctor.getClinic().getId()) == null) {
            throw new IdInvalidException("Clinic với id :" + reqDoctor.getClinic().getId() + " không tồn tại");
        }

        if (this.specialtyService.fetchSpecialtyById(reqDoctor.getSpecialty().getId()) == null) {
            throw new IdInvalidException("Specialty với id :" + reqDoctor.getSpecialty().getId() + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.doctorService.convertToResDoctorDTO(this.doctorService.handleUpdateDoctor(reqDoctor)));
    }

    @GetMapping("/doctors/{id}")
    @ApiMessage("Fetch doctor by id")
    public ResponseEntity<ResDoctorDTO> getDoctorById(@PathVariable("id") long id) throws IdInvalidException {
        Doctor doctor = this.doctorService.fetchDoctorById(id);

        if (doctor == null) {
            throw new IdInvalidException("Doctor với id " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.doctorService.convertToResDoctorDTO(doctor));
    }

    @DeleteMapping("/doctors/{id}")
    @ApiMessage("Delete doctor by id")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable("id") long id)
            throws IdInvalidException {
        Doctor doctor = this.doctorService.fetchDoctorById(id);

        if (doctor == null) {
            throw new IdInvalidException("Doctor với id " + id + " không tồn tại");
        }
        this.doctorService.handleDeleteDoctor(doctor);

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
}
