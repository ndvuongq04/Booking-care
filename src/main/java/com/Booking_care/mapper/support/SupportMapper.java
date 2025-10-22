package com.Booking_care.mapper.support;

import com.Booking_care.domain.Support;
import com.Booking_care.domain.dto.SupportDTO.ResSupportDTO;
import com.Booking_care.mapper.account.AccountMapper;
import com.Booking_care.mapper.clinic.ClinicMapper;

/**
 * Mapper for Support entity
 */
public final class SupportMapper {
    
    private SupportMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Support entity to ResSupportDTO
     * @param support Support entity
     * @return ResSupportDTO
     */
    public static ResSupportDTO toResSupportDTO(Support support) {
        if (support == null) {
            return null;
        }

        ResSupportDTO res = new ResSupportDTO();
        res.setId(support.getId());
        res.setIsActive(support.getIsActive());

        if (support.getAccount() != null) {
            res.setAccount(AccountMapper.toResAccountDTO(support.getAccount()));
        }

        if (support.getClinic() != null) {
            res.setClinic(ClinicMapper.toResClinicDTO(support.getClinic()));
        }

        return res;
    }
}

