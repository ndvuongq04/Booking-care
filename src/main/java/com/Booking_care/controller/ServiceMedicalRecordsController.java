package com.Booking_care.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.ServiceMedicalRecord;
import com.Booking_care.domain.dto.ServiceMedicalRecordDTO.ReqServiceMedicalRecordDTO;
import com.Booking_care.domain.dto.ServiceMedicalRecordDTO.ResServiceMedicalRecordDTO;
import com.Booking_care.service.ServiceMedicalRecordsService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.servicemedicalrecord.ServiceMedicalRecordMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ServiceMedicalRecordsController {
    private final ServiceMedicalRecordsService serviceMedicalRecordsService;

    public ServiceMedicalRecordsController(ServiceMedicalRecordsService serviceMedicalRecordsService) {
        this.serviceMedicalRecordsService = serviceMedicalRecordsService;
    }

    @GetMapping("/service-medical-records/{id}")
    @ApiMessage("Get serviceMedicalRecord by id")
    public ResponseEntity<ResServiceMedicalRecordDTO> getById(@PathVariable Long id) throws IdInvalidException {
        ServiceMedicalRecord smr = this.serviceMedicalRecordsService.fetchById(id);
        return ResponseEntity.ok(ServiceMedicalRecordMapper.toResServiceMedicalRecordDTO(smr));
    }

    @GetMapping("/service-medical-record/{medicalRecordId}")
    @ApiMessage("Get all services by medicalRecordId")
    public ResponseEntity<List<ResServiceMedicalRecordDTO>> getServicesByMedicalRecordId(
            @PathVariable Long medicalRecordId) throws IdInvalidException {
        return ResponseEntity.ok(serviceMedicalRecordsService.getServicesByMedicalRecordId(medicalRecordId));
    }

    @PostMapping("/service-medical-record")
    @ApiMessage("Add multiple services to a medicalRecord")
    public ResponseEntity<List<ResServiceMedicalRecordDTO>> addMultipleServices(
            @Valid @RequestBody ReqServiceMedicalRecordDTO dto) throws IdInvalidException {

        List<ServiceMedicalRecord> list = serviceMedicalRecordsService.addServicesToMedicalRecord(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(list.stream().map(ServiceMedicalRecordMapper::toResServiceMedicalRecordDTO).toList());
    }

}
