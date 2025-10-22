package com.Booking_care.mapper.services;

import com.Booking_care.domain.Services;
import com.Booking_care.domain.dto.ServicesDTO.ResServicesDTO;

/**
 * Mapper for Services entity
 */
public final class ServicesMapper {
    
    private ServicesMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Services entity to ResServicesDTO
     * @param services Services entity
     * @return ResServicesDTO
     */
    public static ResServicesDTO toResServicesDTO(Services services) {
        if (services == null) {
            return null;
        }

        ResServicesDTO res = new ResServicesDTO();
        res.setId(services.getId());
        res.setName(services.getName());
        res.setDescription(services.getDescription());
        res.setCost(services.getCost());
        res.setCreateAt(services.getCreateAt());
        res.setUpdateAt(services.getUpdateAt());

        return res;
    }
}

