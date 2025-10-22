package com.Booking_care.mapper.clinic;

import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;

/**
 * Mapper for Clinic entity
 */
public final class ClinicMapper {

    private ClinicMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Clinic entity to ResClinicDTO
     * 
     * @param clinic Clinic entity
     * @return ResClinicDTO
     */
    public static ResClinicDTO toResClinicDTO(Clinic clinic) {
        if (clinic == null) {
            return null;
        }

        ResClinicDTO res = new ResClinicDTO();
        res.setId(clinic.getId());
        res.setName(clinic.getName());
        res.setDescription(clinic.getDescription());
        res.setPosition(clinic.getPosition());
        res.setPhoneNumber(clinic.getPhoneNumber());
        res.setImage(clinic.getImage());

        if (clinic.getAddress() != null) {
            res.setAddress(new ResClinicDTO.ResAddressDTO(
                    clinic.getAddress().getId(),
                    clinic.getAddress().getCity()));
        }

        return res;
    }
}
