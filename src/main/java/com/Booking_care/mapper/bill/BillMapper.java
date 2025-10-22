package com.Booking_care.mapper.bill;

import java.util.List;

import com.Booking_care.domain.Bill;
import com.Booking_care.domain.BillDetail;
import com.Booking_care.domain.dto.BillDTO.ResBillDTO;
import com.Booking_care.domain.dto.BillDetailDTO.ResBillDetailDTO;
import com.Booking_care.mapper.billdetail.BillDetailMapper;

/**
 * Mapper for Bill entity
 */
public final class BillMapper {
    
    private BillMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Bill entity to ResBillDTO
     * @param bill Bill entity
     * @param billDetails List of BillDetail entities
     * @return ResBillDTO
     */
    public static ResBillDTO toResBillDTO(Bill bill, List<BillDetail> billDetails) {
        if (bill == null) {
            return null;
        }

        ResBillDTO dto = new ResBillDTO();
        dto.setId(bill.getId());

        // Patient
        if (bill.getPatient() != null) {
            dto.setPatient(new ResBillDTO.PatientDTO(
                    bill.getPatient().getId(),
                    bill.getPatient().getAccount().getName()));
        }

        // MedicalRecord
        if (bill.getMedicalRecord() != null) {
            dto.setMedicalRecord(new ResBillDTO.MedicalRecordDTO(
                    bill.getMedicalRecord().getId(),
                    bill.getMedicalRecord().getDescription()));
        }

        // Support
        if (bill.getSupport() != null) {
            dto.setSupport(new ResBillDTO.SupportDTO(
                    bill.getSupport().getId(),
                    bill.getSupport().getAccount().getName()));
        }

        // BillDetails
        if (billDetails != null) {
            List<ResBillDetailDTO> services = billDetails.stream()
                    .map(BillDetailMapper::toResBillDetailDTO)
                    .toList();
            dto.setServices(services);
        }

        // Tổng tiền
        dto.setTotalBill(bill.getTotalBill());
        dto.setStatus(bill.getStatus());

        dto.setCreateAt(bill.getCreateAt());
        dto.setUpdateAt(bill.getUpdateAt());

        return dto;
    }
}

