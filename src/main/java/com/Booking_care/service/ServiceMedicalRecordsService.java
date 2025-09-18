package com.Booking_care.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Booking_care.domain.MedicalRecord;
import com.Booking_care.domain.ServiceMedicalRecord;
import com.Booking_care.domain.Services;
import com.Booking_care.domain.dto.ServiceMedicalRecordDTO.ReqServiceMedicalRecordDTO;
import com.Booking_care.domain.dto.ServiceMedicalRecordDTO.ResServiceMedicalRecordDTO;
import com.Booking_care.repository.ServiceMedicalRecordsRepository;
import com.Booking_care.util.error.IdInvalidException;

@Service
public class ServiceMedicalRecordsService {

    private final ServiceMedicalRecordsRepository serviceMedicalRecordsRepository;
    private final ServicesService servicesService;
    private final MedicalRecordsService medicalRecordsService;

    public ServiceMedicalRecordsService(ServiceMedicalRecordsRepository serviceMedicalRecordsRepository,
            ServicesService servicesService,
            MedicalRecordsService medicalRecordsService) {
        this.serviceMedicalRecordsRepository = serviceMedicalRecordsRepository;
        this.servicesService = servicesService;
        this.medicalRecordsService = medicalRecordsService;

    }

    public ServiceMedicalRecord fetchById(Long id) throws IdInvalidException {
        return serviceMedicalRecordsRepository.findById(id).orElse(null);
    }

    public ResServiceMedicalRecordDTO toDto(ServiceMedicalRecord smr) {
        ResServiceMedicalRecordDTO dto = new ResServiceMedicalRecordDTO();
        dto.setId(smr.getId());
        dto.setCreateAt(smr.getCreateAt());
        dto.setUpdateAt(smr.getUpdateAt());

        if (smr.getService() != null) {
            ResServiceMedicalRecordDTO.ServiceDTO sDto = new ResServiceMedicalRecordDTO.ServiceDTO();
            sDto.setId(smr.getService().getId());
            sDto.setName(smr.getService().getName());
            sDto.setCost(smr.getService().getCost());
            sDto.setDescription(smr.getService().getDescription());
            dto.setService(sDto);
        }
        return dto;
    }

    public List<ResServiceMedicalRecordDTO> getServicesByMedicalRecordId(Long medicalRecordId)
            throws IdInvalidException {
        this.medicalRecordsService.fetchMedicalRecordById(medicalRecordId);

        List<ServiceMedicalRecord> list = serviceMedicalRecordsRepository.findByMedicalRecordId(medicalRecordId);
        return list.stream().map(item -> this.toDto(item)).toList();
    }

    @Transactional
    public List<ServiceMedicalRecord> addServicesToMedicalRecord(ReqServiceMedicalRecordDTO dto)
            throws IdInvalidException {

        MedicalRecord medicalRecord = this.medicalRecordsService.fetchMedicalRecordById(dto.getMedicalRecordId());
        if (medicalRecord == null) {
            throw new IdInvalidException("MedicalRecord với id : " + dto.getMedicalRecordId() + " không tồn tại");
        }

        List<ServiceMedicalRecord> result = new ArrayList<>();

        for (Long serviceId : dto.getServiceIds()) {
            Services service = this.servicesService.fetchServicesById(serviceId);

            if (service == null) {
                throw new IdInvalidException("Service với id : " + serviceId + " không tồn tại");
            }

            ServiceMedicalRecord smr = new ServiceMedicalRecord();
            smr.setMedicalRecord(medicalRecord);
            smr.setService(service);

            result.add(serviceMedicalRecordsRepository.save(smr));
        }

        return result;
    }

}
