package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.Support;
import com.Booking_care.domain.dto.SupportDTO.ResSupportDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.SupportRepository;

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

    public boolean isAccountExits(long id) {
        return this.supportRepository.existsByAccountId(id);
    }

    public Account fetchAccountById(long id) {
        return this.accountService.fetchAccountById(id);
    }

    public Support handleCreateSupport(Support support) {
        return this.supportRepository.save(support);
    }

    public ResSupportDTO convertToResSupportDTO(Support support) {
        ResSupportDTO res = new ResSupportDTO();
        Account acc = fetchAccountById(support.getAccount().getId());
        res.setId(support.getId());
        res.setIsActive(support.getIsActive());
        res.setAccount(this.accountService.convertToResAccountDTO(acc));
        res.setClinic(this.clinicService.convertToClinicDTO(support.getClinic()));
        return res;
    }

    public Support fetchSupportById(long id) {
        Optional<Support> sup = this.supportRepository.findById(id);
        if (sup.isPresent()) {
            return sup.get();
        }
        return null;
    }

    public Support handleUpdateSupport(Support support) {
        Support currentSupport = this.fetchSupportById(support.getId());
        if (currentSupport != null) {

            if (support.getClinic() != null) {
                Clinic clinic = this.clinicService.fetchClinicById(support.getClinic().getId());
                currentSupport.setClinic(clinic != null ? clinic : null);
            }

            currentSupport = this.supportRepository.save(currentSupport);
        }
        return currentSupport;
    }

    public void handleDeleteSupport(Support s) {
        s.setIsActive(false);
        this.supportRepository.save(s);
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
                .map(item -> this.convertToResSupportDTO(item))
                .collect(Collectors.toList());

        res.setResult(listDoc);
        res.setMeta(meta);

        return res;
    }
}
