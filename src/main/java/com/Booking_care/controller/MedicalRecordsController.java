package com.Booking_care.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.MedicalRecord;
import com.Booking_care.domain.dto.AccountDTO.CreateAccountDTO;
import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ReqMedicalRecordDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.ClinicService;
import com.Booking_care.service.DoctorService;
import com.Booking_care.service.MedicalRecordsService;
import com.Booking_care.service.PatientService;
import com.Booking_care.service.SpecialtyService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class MedicalRecordsController {

    private final MedicalRecordsService medicalRecordsService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;

    public MedicalRecordsController(MedicalRecordsService medicalRecordsService,
            PatientService patientService,
            DoctorService doctorService,
            ClinicService clinicService,
            SpecialtyService specialtyService) {
        this.medicalRecordsService = medicalRecordsService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.clinicService = clinicService;
        this.specialtyService = specialtyService;

    }

    @GetMapping("/medicalRecord")
    @ApiMessage("Fetch all medicalRecord")
    public ResponseEntity<ResultPaginationDTO> getAllMedicalRecord(
            Pageable pageable) {
        ResultPaginationDTO result = this.medicalRecordsService.fetchAllMedicalRecords(pageable);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/medicalRecord")
    @ApiMessage("Create new medicalRecord")
    public ResponseEntity<ResMedicalRecordDTO> createNewMedicalRecord(@Valid @RequestBody ReqMedicalRecordDTO reqRecord)
            throws IdInvalidException {

        if (this.clinicService.fetchClinicById(reqRecord.getClinicId()) == null) {
            throw new IdInvalidException("Clinic với id : " + reqRecord.getClinicId() + " không tồn tại");
        }

        if (this.doctorService.fetchDoctorById(reqRecord.getDoctorId()) == null) {
            throw new IdInvalidException("Doctor với id : " + reqRecord.getDoctorId() + " không tồn tại");
        }

        if (this.specialtyService.fetchSpecialtyById(reqRecord.getSpecialtyId()) == null) {
            throw new IdInvalidException("Specialty với id : " + reqRecord.getSpecialtyId() + " không tồn tại");
        }

        if (this.patientService.fetchPatientById(reqRecord.getPatientId()) == null) {
            throw new IdInvalidException("Patient với id : " + reqRecord.getPatientId() + " không tồn tại");
        }

        MedicalRecord mRecord = this.medicalRecordsService.handleCreateMedicalRecord(reqRecord);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.medicalRecordsService.convertToMedicalRecordDTO(mRecord));
    }

    @GetMapping("/medicalRecord/{id}")
    @ApiMessage("Fetch medicalRecord by id")
    public ResponseEntity<ResMedicalRecordDTO> getMedicalRecordById(@PathVariable("id") Long id)
            throws IdInvalidException {

        MedicalRecord record = this.medicalRecordsService.fetchMedicalRecordById(id);
        if (record == null) {
            throw new IdInvalidException("Id : " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.medicalRecordsService.convertToMedicalRecordDTO(record));
    }

    @PutMapping("/medicalRecord")
    @ApiMessage("Update medicalRecord")
    public ResponseEntity<ResMedicalRecordDTO> updateMedicalRecord(@Valid @RequestBody ReqMedicalRecordDTO reqRecord)
            throws IdInvalidException {

        if (this.clinicService.fetchClinicById(reqRecord.getClinicId()) == null) {
            throw new IdInvalidException("Clinic với id : " + reqRecord.getClinicId() + " không tồn tại");
        }

        if (this.doctorService.fetchDoctorById(reqRecord.getDoctorId()) == null) {
            throw new IdInvalidException("Doctor với id : " + reqRecord.getDoctorId() + " không tồn tại");
        }

        if (this.specialtyService.fetchSpecialtyById(reqRecord.getSpecialtyId()) == null) {
            throw new IdInvalidException("Specialty với id : " + reqRecord.getSpecialtyId() + " không tồn tại");
        }

        if (this.patientService.fetchPatientById(reqRecord.getPatientId()) == null) {
            throw new IdInvalidException("Patient với id : " + reqRecord.getPatientId() + " không tồn tại");
        }

        MedicalRecord updated = this.medicalRecordsService.handleUpdateMedicalRecord(reqRecord.getId(), reqRecord);
        return ResponseEntity.ok(this.medicalRecordsService.convertToMedicalRecordDTO(updated));
    }

    @GetMapping("/medicalRecord/doctor/{id}")
    @ApiMessage("Fetch all medicalRecord by doctor")
    public ResponseEntity<ResultPaginationDTO> getAllMedicalRecordByDoctor(
            Pageable pageable, @PathVariable("id") long doctorId) {
        ResultPaginationDTO result = this.medicalRecordsService.fetchAllMedicalRecordsByDoctor(pageable, doctorId);
        return ResponseEntity.ok().body(result);
    }

}
