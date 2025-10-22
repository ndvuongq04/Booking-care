package com.Booking_care.mapper.servicemedicalrecord;

import com.Booking_care.domain.ServiceMedicalRecord;
import com.Booking_care.domain.dto.ServiceMedicalRecordDTO.ResServiceMedicalRecordDTO;

/**
 * Mapper for ServiceMedicalRecord entity
 */
public final class ServiceMedicalRecordMapper {
    
    private ServiceMedicalRecordMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert ServiceMedicalRecord entity to ResServiceMedicalRecordDTO
     * @param smr ServiceMedicalRecord entity
     * @return ResServiceMedicalRecordDTO
     */
    public static ResServiceMedicalRecordDTO toResServiceMedicalRecordDTO(ServiceMedicalRecord smr) {
        if (smr == null) {
            return null;
        }

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
}

