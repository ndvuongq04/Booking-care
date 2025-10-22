package com.Booking_care.mapper.doctor;

import com.Booking_care.domain.Doctor;
import com.Booking_care.domain.dto.DoctorDTO.ResDoctorDTO;
import com.Booking_care.mapper.account.AccountMapper;
import com.Booking_care.mapper.clinic.ClinicMapper;
import com.Booking_care.mapper.specialty.SpecialtyMapper;

/**
 * Mapper for Doctor entity
 */
public final class DoctorMapper {
    
    private DoctorMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Doctor entity to ResDoctorDTO
     * @param doctor Doctor entity
     * @return ResDoctorDTO
     */
    public static ResDoctorDTO toResDoctorDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        ResDoctorDTO dto = new ResDoctorDTO();
        dto.setId(doctor.getId());
        dto.setDegree(doctor.getDegree() != null ? doctor.getDegree().name() : null);
        dto.setIsActive(doctor.getIsActive());
        dto.setCreateAt(doctor.getCreateAt());
        dto.setUpdateAt(doctor.getUpdateAt());
        dto.setDescription(doctor.getDescription());
        dto.setCost(doctor.getCost());

        if (doctor.getAccount() != null) {
            dto.setAccount(AccountMapper.toResAccountDTO(doctor.getAccount()));
        }

        if (doctor.getClinic() != null) {
            dto.setClinic(ClinicMapper.toResClinicDTO(doctor.getClinic()));
        }

        if (doctor.getSpecialty() != null) {
            dto.setSpecialty(SpecialtyMapper.toResSpecialtyDTO(doctor.getSpecialty()));
        }

        return dto;
    }
}

