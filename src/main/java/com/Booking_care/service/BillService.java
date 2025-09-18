package com.Booking_care.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Booking_care.domain.Services;
import com.Booking_care.domain.dto.BillDTO.ReqBillDTO;
import com.Booking_care.domain.dto.BillDTO.ResBillDTO;
import com.Booking_care.domain.dto.BillDTO.ReqBillDTO.ServiceItemDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO;
import com.Booking_care.domain.dto.MedicalRecordDTO.ResMedicalRecordDTO.PatientDTO;
import com.Booking_care.domain.dto.SupportDTO.ResSupportDTO;
import com.Booking_care.repository.BillRepository;
import com.Booking_care.util.error.IdInvalidException;

@Service
public class BillService {
    private final BillRepository billRepository;
    private final ServicesService servicesService;

    public BillService(BillRepository billRepository,
            ServicesService servicesService) {
        this.billRepository = billRepository;
        this.servicesService = servicesService;
    }

    public ResBillDTO toDto(ReqBillDTO reqBill) throws IdInvalidException {
        ResBillDTO res = new ResBillDTO();

        // Patient
        if (reqBill.getPatientId() > 0) {
            PatientDTO p = new PatientDTO();
            p.setId(reqBill.getPatientId());
            res.setPatient(p);
        }

        // Medical Record
        if (reqBill.getMedicalRecordId() > 0) {
            ResMedicalRecordDTO m = new ResMedicalRecordDTO();
            m.setId(reqBill.getMedicalRecordId());
            res.setMedicalRecord(m);
        }

        // Support
        if (reqBill.getSupportId() > 0) {
            ResSupportDTO s = new ResSupportDTO();
            s.setId(reqBill.getSupportId());
            res.setSupport(s);
        }

        // Status
        res.setStatus(reqBill.getStatus());

        // Services
        if (reqBill.getServices() != null && !reqBill.getServices().isEmpty()) {
            List<ResBillDTO.ServiceItemDTO> serviceItems = new ArrayList<>();

            for (ReqBillDTO.ServiceItemDTO serviceItem : reqBill.getServices()) {
                Services s = this.servicesService.fetchServicesById(serviceItem.getServiceId());
                if (s == null) {
                    throw new IdInvalidException("Service với id : " + serviceItem.getServiceId() + " không tồn tại");
                }

                ResBillDTO.ServiceItemDTO serviceItemDTO = new ResBillDTO.ServiceItemDTO();
                serviceItemDTO.setService(this.servicesService.handleConvertToResServicesDTO(s));
                serviceItemDTO.setQuantity(serviceItem.getQuantity());

                serviceItems.add(serviceItemDTO);
            }

            res.setServices(serviceItems);
        } else {
            res.setServices(Collections.emptyList());
        }

        return res;
    }

}
