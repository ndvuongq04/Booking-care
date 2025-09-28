package com.Booking_care.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Booking_care.domain.Bill;
import com.Booking_care.domain.BillDetail;
import com.Booking_care.domain.MedicalRecord;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.Support;
import com.Booking_care.domain.dto.BillDTO.ReqBillDTO;
import com.Booking_care.domain.dto.BillDTO.ResBillDTO;
import com.Booking_care.domain.dto.BillDetailDTO.ResBillDetailDTO;
import com.Booking_care.domain.enums.BillStatusEnum;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.BillRepository;
import com.Booking_care.util.error.IdInvalidException;

@Service
public class BillService {
    private final BillRepository billRepository;
    private final ServicesService servicesService;
    private final BillDetailService billDetailService;
    private final PatientService patientService;
    private final MedicalRecordsService medicalRecordsService;
    private final SupportService supportService;

    public BillService(BillRepository billRepository,
            ServicesService servicesService,
            BillDetailService billDetailService,
            PatientService patientService,
            MedicalRecordsService medicalRecordsService,
            SupportService supportService) {
        this.billRepository = billRepository;
        this.servicesService = servicesService;
        this.billDetailService = billDetailService;
        this.patientService = patientService;
        this.medicalRecordsService = medicalRecordsService;
        this.supportService = supportService;
    }

    @Transactional
    public ResBillDTO createBill(ReqBillDTO reqBillDTO) throws IdInvalidException {
        Bill bill = new Bill();

        // Patient
        Patient patient = this.patientService.fetchPatientById(reqBillDTO.getPatientId());
        if (patient == null) {
            throw new IdInvalidException("Patient với id : " + reqBillDTO.getPatientId() + " không tồn tại");
        }
        bill.setPatient(patient);

        // Medical Record
        MedicalRecord medicalRecord = this.medicalRecordsService
                .fetchMedicalRecordById(reqBillDTO.getMedicalRecordId());
        if (medicalRecord == null) {
            throw new IdInvalidException(
                    "MedicalRecord với id : " + reqBillDTO.getMedicalRecordId() + " không tồn tại");
        }
        bill.setMedicalRecord(medicalRecord);

        // Support
        Support support = this.supportService.fetchSupportById(reqBillDTO.getSupportId());
        if (support == null) {
            throw new IdInvalidException("Support với id : " + reqBillDTO.getSupportId() + " không tồn tại");
        }
        bill.setSupport(support);

        // Status
        bill.setStatus(BillStatusEnum.PAID);

        // Save Bill trước để có ID
        Bill savedBill = this.billRepository.save(bill);
        reqBillDTO.setId(savedBill.getId());

        List<BillDetail> billDetails = this.billDetailService.handleCreateBillDetail(reqBillDTO, savedBill);
        // Total Bill
        BigDecimal totalBill = billDetails.stream()
                .map(item -> item.getTotalService())
                .filter(item -> item != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);// cong gop , bat dau tu 0

        savedBill.setTotalBill(totalBill);
        this.billRepository.save(savedBill);

        return this.toResBillDTO(savedBill, billDetails);
    }

    public Bill fetchById(long id) {
        return this.billRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO handleGetAllBill(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Bill> page = this.billRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        // service
        List<ResBillDTO> listBill = page.getContent().stream()
                .map(bill -> {
                    List<BillDetail> billDetails = this.billDetailService.fetchBillDetailByBillId(bill.getId());
                    return this.toResBillDTO(bill, billDetails);
                })
                .toList();

        res.setResult(listBill);
        res.setMeta(meta);

        return res;
    }

    public ResBillDTO getBillById(Long id) throws IdInvalidException {
        Bill b = this.fetchById(id);
        if (b == null) {
            throw new IdInvalidException("Bill với id : " + id + " không tồn tại");
        }

        List<BillDetail> billDetails = this.billDetailService.fetchBillDetailByBillId(id);

        return this.toResBillDTO(b, billDetails);
    }

    public ResBillDTO toResBillDTO(Bill bill, List<BillDetail> billDetails) {
        if (bill == null)
            return null;

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
        List<ResBillDetailDTO> services = billDetails.stream()
                .map(item -> this.billDetailService.toResBillDetailDTO(item))
                .toList();
        dto.setServices(services);

        // Tổng tiền
        dto.setTotalBill(bill.getTotalBill());
        dto.setStatus(bill.getStatus());

        dto.setCreateAt(bill.getCreateAt());
        dto.setUpdateAt(bill.getUpdateAt());

        return dto;
    }

    public ResultPaginationDTO getBillByPatientId(Long id, Pageable pageable) throws IdInvalidException {
        Patient p = this.patientService.fetchPatientById(id);
        if (p == null) {
            throw new IdInvalidException("Patient với id : " + id + " không tồn tại");
        }

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Bill> page = this.billRepository.findByPatientId(id, pageable);

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        List<ResBillDTO> listBill = page.getContent().stream()
                .map(bill -> {
                    List<BillDetail> billDetails = this.billDetailService.fetchBillDetailByBillId(bill.getId());
                    return this.toResBillDTO(bill, billDetails);
                })
                .toList();

        res.setMeta(meta);
        res.setResult(listBill);

        return res;
    }

}
