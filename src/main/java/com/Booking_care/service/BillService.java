package com.Booking_care.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Booking_care.domain.Bill;
import com.Booking_care.domain.BillDetail;
import com.Booking_care.domain.Patient;
import com.Booking_care.domain.Support;
import com.Booking_care.domain.dto.BillDTO.BillClinicCriteriaDTO;
import com.Booking_care.domain.dto.BillDTO.BillCriteriaDTO;
import com.Booking_care.domain.dto.BillDTO.ReqBillDTO;
import com.Booking_care.domain.dto.BillDTO.ResBillDTO;
import com.Booking_care.domain.enums.BillStatusEnum;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.mapper.bill.BillMapper;
import com.Booking_care.repository.BillRepository;
import com.Booking_care.service.specification.BillSpecs;
import com.Booking_care.util.error.IdInvalidException;

@Service
public class BillService {
    private final BillRepository billRepository;
    private final BillDetailService billDetailService;
    private final PatientService patientService;
    private final SupportService supportService;
    private final ClinicService clinicService;

    public BillService(BillRepository billRepository,
            BillDetailService billDetailService,
            PatientService patientService,
            SupportService supportService,
            ClinicService clinicService) {
        this.billRepository = billRepository;
        this.billDetailService = billDetailService;
        this.patientService = patientService;
        this.supportService = supportService;
        this.clinicService = clinicService;
    }

    @Transactional
    public ResBillDTO createBill(ReqBillDTO reqBillDTO) throws IdInvalidException {
        // Validation: các services sẽ throw exception nếu không tìm thấy
        Patient patient = this.patientService.fetchPatientById(reqBillDTO.getPatientId());
        Support support = this.supportService.fetchSupportById(reqBillDTO.getSupportId());

        try {
            Bill bill = new Bill();
            bill.setPatient(patient);
            bill.setSupport(support);
            bill.setStatus(BillStatusEnum.PAID);

            // Save Bill trước để có ID
            Bill savedBill = this.billRepository.save(bill);
            reqBillDTO.setId(savedBill.getId());

            List<BillDetail> billDetails = this.billDetailService.handleCreateBillDetail(reqBillDTO, savedBill);
            // Total Bill
            BigDecimal totalBill = billDetails.stream()
                    .map(item -> item.getTotalService())
                    .filter(item -> item != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            savedBill.setTotalBill(totalBill);
            this.billRepository.save(savedBill);

            return BillMapper.toResBillDTO(savedBill, billDetails);
        } catch (IdInvalidException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo bill: " + e.getMessage());
        }
    }

    public Bill fetchById(long id) {
        return this.billRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Bill với id " + id + " không tồn tại"));
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
                    return BillMapper.toResBillDTO(bill, billDetails);
                })
                .toList();

        res.setResult(listBill);
        res.setMeta(meta);

        return res;
    }

    public ResBillDTO getBillById(Long id) throws IdInvalidException {
        Bill b = this.fetchById(id);
        List<BillDetail> billDetails = this.billDetailService.fetchBillDetailByBillId(id);
        return BillMapper.toResBillDTO(b, billDetails);
    }

    public ResultPaginationDTO getBillByPatientId(Long id, Pageable pageable) throws IdInvalidException {
        // Validation: patientService sẽ throw exception nếu không tìm thấy
        this.patientService.fetchPatientById(id);

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
                    return BillMapper.toResBillDTO(bill, billDetails);
                })
                .toList();

        res.setMeta(meta);
        res.setResult(listBill);

        return res;
    }

    public ResultPaginationDTO getBillByClinicId(Long id, Pageable pageable) throws IdInvalidException {
        // Validation: clinicService sẽ throw exception nếu không tìm thấy
        this.clinicService.fetchClinicById(id);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Bill> page = this.billRepository.findBySupport_Clinic_Id(id, pageable);

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        List<ResBillDTO> listBill = page.getContent().stream()
                .map(bill -> {
                    List<BillDetail> billDetails = this.billDetailService.fetchBillDetailByBillId(bill.getId());
                    return BillMapper.toResBillDTO(bill, billDetails);
                })
                .toList();

        res.setMeta(meta);
        res.setResult(listBill);

        return res;
    }

    public Page<Bill> getAllBillSearch(Pageable pageable, BillCriteriaDTO billCriteriaDTO) {
        Specification<Bill> combinedSpec = Specification.where(null);

        if (billCriteriaDTO.getBillId() != null) {
            Specification<Bill> currentSpec = BillSpecs.billIdEqual(billCriteriaDTO.getBillId());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (billCriteriaDTO.getMonthYear() != null) {
            YearMonth monthYear = billCriteriaDTO.getMonthYear();

            Instant from = monthYear.atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng
                    .toInstant();

            Instant to = monthYear.plusMonths(1).atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng kế tiếp
                    .toInstant();

            Specification<Bill> currentSpec = BillSpecs.dateBetween(from, to);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (billCriteriaDTO.getAccountName() != null && !billCriteriaDTO.getAccountName().trim().isEmpty()) {
            Specification<Bill> currentSpec = BillSpecs
                    .patientAccountNameLikeIgnoreCase(billCriteriaDTO.getAccountName());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (billCriteriaDTO.getServiceId() != null) {
            Specification<Bill> currentSpec = BillSpecs.serviceIdJoinEqual(billCriteriaDTO.getServiceId());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.billRepository.findAll(combinedSpec, pageable);
    }

    public ResultPaginationDTO handleGetAllBillSearch(Pageable pageable, BillCriteriaDTO billCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Bill> page = this.getAllBillSearch(pageable, billCriteriaDTO);

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
                    return BillMapper.toResBillDTO(bill, billDetails);
                })
                .toList();

        res.setResult(listBill);
        res.setMeta(meta);

        return res;
    }

    public Page<Bill> getAllBillByClinicIdSearch(Pageable pageable, BillClinicCriteriaDTO billClinicCriteriaDTO) {
        if (billClinicCriteriaDTO.getClinicId() == null) {
            return Page.empty(pageable);
        }

        Specification<Bill> combinedSpec = Specification.where(
                BillSpecs.clinicIdJoinEqual(billClinicCriteriaDTO.getClinicId()));

        if (billClinicCriteriaDTO.getPhoneNumber() != null
                && !billClinicCriteriaDTO.getPhoneNumber().trim().isEmpty()) {
            combinedSpec = combinedSpec.and(
                    BillSpecs.patientAccountPhoneLike(billClinicCriteriaDTO.getPhoneNumber().trim()));
        }

        if (billClinicCriteriaDTO.getEmail() != null && !billClinicCriteriaDTO.getEmail().trim().isEmpty()) {
            combinedSpec = combinedSpec.and(
                    BillSpecs.patientAccountEmailLike(billClinicCriteriaDTO.getEmail().trim()));
        }

        if (billClinicCriteriaDTO.getCccd() != null && !billClinicCriteriaDTO.getCccd().trim().isEmpty()) {
            combinedSpec = combinedSpec.and(
                    BillSpecs.patientAccountCccdLike(billClinicCriteriaDTO.getCccd().trim()));
        }

        return this.billRepository.findAll(combinedSpec, pageable);
    }

    public ResultPaginationDTO getBillByClinicIdSearch(BillClinicCriteriaDTO billClinicCriteriaDTO, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Bill> page = this.getAllBillByClinicIdSearch(pageable, billClinicCriteriaDTO);

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
                    return BillMapper.toResBillDTO(bill, billDetails);
                })
                .toList();

        res.setResult(listBill);
        res.setMeta(meta);

        return res;
    }

}
