package com.Booking_care.mapper.patient;

import com.Booking_care.domain.Patient;
import com.Booking_care.domain.dto.PatientDTO.ResPatientDTO;
import com.Booking_care.mapper.account.AccountMapper;

/**
 * Mapper for Patient entity
 */
public final class PatientMapper {

    private PatientMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Patient entity to ResPatientDTO
     * 
     * @param patient Patient entity
     * @return ResPatientDTO
     */
    public static ResPatientDTO toResPatientDTO(Patient patient) {
        if (patient == null) {
            return null;
        }

        ResPatientDTO res = new ResPatientDTO();
        res.setId(patient.getId());
        res.setBhyt(patient.getBhyt());
        res.setIsActive(patient.getIsActive());

        if (patient.getAccount() != null) {
            res.setAccount(AccountMapper.toResAccountDTO(patient.getAccount()));
        }

        return res;
    }
}
