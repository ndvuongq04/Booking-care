package com.Booking_care.mapper.role;

import com.Booking_care.domain.Role;
import com.Booking_care.domain.dto.ResRoleDTO;

/**
 * Mapper for Role entity
 */
public final class RoleMapper {
    
    private RoleMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Role entity to ResRoleDTO
     * @param role Role entity
     * @return ResRoleDTO
     */
    public static ResRoleDTO toResRoleDTO(Role role) {
        if (role == null) {
            return null;
        }

        ResRoleDTO res = new ResRoleDTO();
        res.setId(role.getId());
        res.setName(role.getName());
        res.setDescription(role.getDescription());

        return res;
    }
}

