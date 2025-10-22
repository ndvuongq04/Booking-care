package com.Booking_care.controller;

import org.springframework.http.HttpStatus;
import com.Booking_care.domain.MedicalRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.service.MedicalRecordsService;
import com.Booking_care.mapper.medicalrecord.MedicalRecordMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.dto.MedicalRecordDTO.MedicalRecordCriteriaDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ReqMedicalRecordDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1")
public class MedicalRecordsController {

    private final MedicalRecordsService medicalRecordsService;

    public MedicalRecordsController(MedicalRecordsService medicalRecordsService) {
        this.medicalRecordsService = medicalRecordsService;
    }

    @GetMapping("/medicalRecord")
    @ApiMessage("Fetch all medicalRecord")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllMedicalRecord(
            Pageable pageable) {
        ResultPaginationDTO result = this.medicalRecordsService.fetchAllMedicalRecords(pageable);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/medicalRecord")
    @ApiMessage("Create new medicalRecord")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ResMedicalRecordDTO> createNewMedicalRecord(@Valid @RequestBody ReqMedicalRecordDTO reqRecord)
            throws IdInvalidException {
        MedicalRecord mRecord = this.medicalRecordsService.handleCreateMedicalRecord(reqRecord);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MedicalRecordMapper.toResMedicalRecordDTO(mRecord));
    }

    @GetMapping("/medicalRecord/{id}")
    @ApiMessage("Fetch medicalRecord by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'CLIENT')")
    public ResponseEntity<ResMedicalRecordDTO> getMedicalRecordById(@PathVariable("id") Long id)
            throws IdInvalidException {
        MedicalRecord record = this.medicalRecordsService.fetchMedicalRecordById(id);
        return ResponseEntity.ok(MedicalRecordMapper.toResMedicalRecordDTO(record));
    }

    @PutMapping("/medicalRecord")
    @ApiMessage("Update medicalRecord")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ResMedicalRecordDTO> updateMedicalRecord(@Valid @RequestBody ReqMedicalRecordDTO reqRecord)
            throws IdInvalidException {
        MedicalRecord updated = this.medicalRecordsService.handleUpdateMedicalRecord(reqRecord.getId(), reqRecord);
        return ResponseEntity.ok(MedicalRecordMapper.toResMedicalRecordDTO(updated));
    }

    @GetMapping("/medicalRecord/doctor/{id}")
    @ApiMessage("Fetch all medicalRecord by doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ResultPaginationDTO> getAllMedicalRecordByDoctor(
            Pageable pageable, @PathVariable("id") long doctorId) {
        ResultPaginationDTO result = this.medicalRecordsService.fetchAllMedicalRecordsByDoctor(pageable, doctorId);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/medicalRecord/doctor/{id}/search")
    @ApiMessage("Fetch all medicalRecord by doctor search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ResultPaginationDTO> getAllMedicalRecordByDoctorSearch(
            Pageable pageable, MedicalRecordCriteriaDTO medicalRecordCriteriaDTO, @PathVariable("id") long id) {

        medicalRecordCriteriaDTO.setDoctorId(id);
        ResultPaginationDTO result = this.medicalRecordsService.fetchAllMedicalRecordsByDoctorSearch(pageable,
                medicalRecordCriteriaDTO);
        return ResponseEntity.ok().body(result);
    }

}
