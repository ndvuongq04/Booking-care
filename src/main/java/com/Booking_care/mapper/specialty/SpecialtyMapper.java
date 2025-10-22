package com.Booking_care.mapper.specialty;

import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.dto.SpecialtyDTO.ResSpecialtyDTO;

/**
 * Mapper for Specialty entity
 */
public final class SpecialtyMapper {
    
    private SpecialtyMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Specialty entity to ResSpecialtyDTO
     * @param specialty Specialty entity
     * @return ResSpecialtyDTO
     */
    public static ResSpecialtyDTO toResSpecialtyDTO(Specialty specialty) {
        if (specialty == null) {
            return null;
        }

        ResSpecialtyDTO res = new ResSpecialtyDTO();
        res.setId(specialty.getId());
        res.setName(specialty.getName());
        res.setDescription(specialty.getDescription());
        res.setImage(specialty.getImage());
        res.setIsActive(specialty.getIsActive());
        res.setCreateAt(specialty.getCreateAt());
        res.setUpdateAt(specialty.getUpdateAt());

        return res;
    }
}

