package com.Booking_care.mapper.billdetail;

import com.Booking_care.domain.BillDetail;
import com.Booking_care.domain.dto.BillDetailDTO.ResBillDetailDTO;
import com.Booking_care.mapper.services.ServicesMapper;

/**
 * Mapper for BillDetail entity
 */
public final class BillDetailMapper {
    
    private BillDetailMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert BillDetail entity to ResBillDetailDTO
     * @param detail BillDetail entity
     * @return ResBillDetailDTO
     */
    public static ResBillDetailDTO toResBillDetailDTO(BillDetail detail) {
        if (detail == null) {
            return null;
        }

        ResBillDetailDTO dto = new ResBillDetailDTO();
        dto.setId(detail.getId());

        if (detail.getService() != null) {
            dto.setService(ServicesMapper.toResServicesDTO(detail.getService()));
        }

        dto.setQuantity(detail.getQuantity());
        dto.setServiceCost(detail.getServiceCost());
        dto.setTotalService(detail.getTotalService());
        dto.setCreateAt(detail.getCreateAt());
        dto.setUpdateAt(detail.getUpdateAt());

        return dto;
    }
}

