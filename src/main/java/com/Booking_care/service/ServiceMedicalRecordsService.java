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
import com.Booking_care.mapper.servicemedicalrecord.ServiceMedicalRecordMapper;

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
        return serviceMedicalRecordsRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("ServiceMedicalRecord với id " + id + " không tồn tại"));
    }


    public List<ResServiceMedicalRecordDTO> getServicesByMedicalRecordId(Long medicalRecordId)
            throws IdInvalidException {
        this.medicalRecordsService.fetchMedicalRecordById(medicalRecordId);

        List<ServiceMedicalRecord> list = serviceMedicalRecordsRepository.findByMedicalRecordId(medicalRecordId);
        return list.stream().map(ServiceMedicalRecordMapper::toResServiceMedicalRecordDTO).toList();
    }

    @Transactional
    public List<ServiceMedicalRecord> addServicesToMedicalRecord(ReqServiceMedicalRecordDTO dto)
            throws IdInvalidException {
        // Validation: check medical record exists (will throw if not found)
        MedicalRecord medicalRecord = this.medicalRecordsService.fetchMedicalRecordById(dto.getMedicalRecordId());

        try {
            List<ServiceMedicalRecord> result = new ArrayList<>();

            for (Long serviceId : dto.getServiceIds()) {
                // Validation: check service exists (will throw if not found)
                Services service = this.servicesService.fetchServicesById(serviceId);

                ServiceMedicalRecord smr = new ServiceMedicalRecord();
                smr.setMedicalRecord(medicalRecord);
                smr.setService(service);

                result.add(serviceMedicalRecordsRepository.save(smr));
            }

            return result;
        } catch (IdInvalidException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể thêm services vào medical record: " + e.getMessage());
        }
    }

}
