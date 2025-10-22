package com.Booking_care.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Support;
import com.Booking_care.domain.dto.SupportDTO.ResSupportDTO;
import com.Booking_care.domain.dto.SupportDTO.SupportCriteriaDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.SupportRepository;
import com.Booking_care.service.specification.SupportSpecs;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.util.error.BusinessException;
import com.Booking_care.mapper.support.SupportMapper;
import com.Booking_care.mapper.clinic.ClinicMapper;

@Service
public class SupportService {
    private final SupportRepository supportRepository;
    private final AccountService accountService;
    private final ClinicService clinicService;

    public SupportService(SupportRepository supportRepository,
            AccountService accountService,
            ClinicService clinicService) {
        this.supportRepository = supportRepository;
        this.accountService = accountService;
        this.clinicService = clinicService;
    }

    public ResClinicDTO getClinicBySupportId(Long supportId) {
        Long clinicId = supportRepository.findClinicIdBySupportId(supportId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Không tìm thấy clinic cho supportId=" + supportId));

        ResClinicDTO res = ClinicMapper.toResClinicDTO(this.clinicService.fetchClinicById(clinicId));

        return res;
    }

    public boolean isAccountExits(long id) {
        return this.supportRepository.existsByAccountId(id);
    }

    public Account fetchAccountById(long id) {
        return this.accountService.fetchAccountById(id);
    }

    public Support handleCreateSupport(Support support) {
        // Validation 1: Check account exists (will throw if not found)
        Account account = this.accountService.fetchAccountById(support.getAccount().getId());
        
        // Validation 2: Check account's role is SUPPORT (FIX BUG - tương tự Doctor/Patient module)
        if (account.getRole() == null || !"SUPPORT".equals(account.getRole().getName())) {
            throw new BusinessException("Chỉ tài khoản với role SUPPORT mới có thể tạo support profile");
        }
        
        // Validation 3: Check account chưa có support profile
        if (this.supportRepository.existsByAccountId(account.getId())) {
            throw new BusinessException("Tài khoản này đã có support profile");
        }

        // Validation 4: Check clinic exists (will throw if not found)
        Clinic clinic = this.clinicService.fetchClinicById(support.getClinic().getId());

        try {
            support.setAccount(account);
            support.setClinic(clinic);
            return this.supportRepository.save(support);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo support: " + e.getMessage());
        }
    }

    public Support fetchSupportById(long id) {
        return this.supportRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Support với id " + id + " không tồn tại"));
    }

    public Support handleUpdateSupport(Support support) {
        // Validation: check support exists (will throw if not found)
        Support currentSupport = this.fetchSupportById(support.getId());

        // Validation: check clinic exists (will throw if not found)
        Clinic clinic = this.clinicService.fetchClinicById(support.getClinic().getId());

        try {
            currentSupport.setClinic(clinic);
            currentSupport.setIsActive(support.getIsActive());
            return this.supportRepository.save(currentSupport);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật support: " + e.getMessage());
        }
    }

    public void handleDeleteSupport(long id) {
        // Validation: check support exists (will throw if not found)
        Support s = this.fetchSupportById(id);

        try {
            s.setIsActive(false);
            this.supportRepository.save(s);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa support: " + e.getMessage());
        }
    }

    public ResultPaginationDTO fetchAllSupport(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Support> page = this.supportRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResSupportDTO> listDoc = page.getContent().stream()
                .map(SupportMapper::toResSupportDTO)
                .collect(Collectors.toList());

        res.setResult(listDoc);
        res.setMeta(meta);

        return res;
    }

    public Page<Support> getAllSupportWithSpec(SupportCriteriaDTO supportCriteriaDTO, Pageable pageable) {
        Specification<Support> combinedSpec = Specification.where(null);

        if (supportCriteriaDTO.getAddress() != null && !supportCriteriaDTO.getAddress().trim().isEmpty()) {
            Specification<Support> currentSpec = SupportSpecs
                    .addressJoinLikeIgnoreCase(supportCriteriaDTO.getAddress());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (supportCriteriaDTO.getClinicId() != null) {
            Specification<Support> currentSpec = SupportSpecs
                    .clinicJointEqual(supportCriteriaDTO.getClinicId());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (supportCriteriaDTO.getName() != null && !supportCriteriaDTO.getName().trim().isEmpty()) {
            Specification<Support> currentSpec = SupportSpecs
                    .nameJoinLikeIgnoreCase(supportCriteriaDTO.getName());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (supportCriteriaDTO.getPhoneNumber() != null && !supportCriteriaDTO.getPhoneNumber().trim().isEmpty()) {
            Specification<Support> currentSpec = SupportSpecs
                    .phoneNumberJoinLikeIgnoreCase(supportCriteriaDTO.getPhoneNumber());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.supportRepository.findAll(combinedSpec, pageable);

    }

    public ResultPaginationDTO fetchAllSupportSearch(Pageable pageable, SupportCriteriaDTO supportCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Support> page = this.getAllSupportWithSpec(supportCriteriaDTO, pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResSupportDTO> listDoc = page.getContent().stream()
                .map(SupportMapper::toResSupportDTO)
                .collect(Collectors.toList());

        res.setResult(listDoc);
        res.setMeta(meta);

        return res;
    }
}
