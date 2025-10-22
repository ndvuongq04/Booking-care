package com.Booking_care.mapper.medicalrecord;

import com.Booking_care.domain.MedicalRecord;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO;

/**
 * Mapper for MedicalRecord entity
 */
public final class MedicalRecordMapper {
    
    private MedicalRecordMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert MedicalRecord entity to ResMedicalRecordDTO
     * @param record MedicalRecord entity
     * @return ResMedicalRecordDTO
     */
    public static ResMedicalRecordDTO toResMedicalRecordDTO(MedicalRecord record) {
        if (record == null) {
            return null;
        }

        ResMedicalRecordDTO dto = new ResMedicalRecordDTO();
        dto.setId(record.getId());
        dto.setDescription(record.getDescription());
        dto.setCreateAt(record.getCreateAt());
        dto.setUpdateAt(record.getUpdateAt());

        // Patient
        if (record.getPatient() != null) {
            ResMedicalRecordDTO.PatientDTO pDto = new ResMedicalRecordDTO.PatientDTO();
            pDto.setId(record.getPatient().getId());
            pDto.setName(record.getPatient().getAccount().getName());
            dto.setPatient(pDto);
        }

        // Doctor
        if (record.getDoctor() != null) {
            ResMedicalRecordDTO.DoctorDTO dDto = new ResMedicalRecordDTO.DoctorDTO();
            dDto.setId(record.getDoctor().getId());
            dDto.setName(record.getDoctor().getAccount().getName());
            dDto.setDegree(record.getDoctor().getDegree().name());
            dto.setDoctor(dDto);
        }

        // Clinic
        if (record.getClinic() != null) {
            ResMedicalRecordDTO.ClinicDTO cDto = new ResMedicalRecordDTO.ClinicDTO();
            cDto.setId(record.getClinic().getId());
            cDto.setName(record.getClinic().getName());
            dto.setClinic(cDto);
        }

        // Specialty
        if (record.getSpecialty() != null) {
            ResMedicalRecordDTO.SpecialtyDTO sDto = new ResMedicalRecordDTO.SpecialtyDTO();
            sDto.setId(record.getSpecialty().getId());
            sDto.setName(record.getSpecialty().getName());
            dto.setSpecialty(sDto);
        }

        return dto;
    }
}

