package com.Booking_care.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Support;
import com.Booking_care.domain.dto.SupportDTO.ResSupportDTO;
import com.Booking_care.domain.dto.SupportDTO.SupportCriteriaDTO;
import com.Booking_care.domain.enums.RoleName;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.AccountProfile;
import com.Booking_care.service.ClinicService;
import com.Booking_care.service.SupportService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/supports")
public class SupportController {
    private final SupportService supportService;
    private final ClinicService clinicService;
    private final AccountProfile accountProfile;

    public SupportController(SupportService supportService, ClinicService clinicService,
            AccountProfile accountProfile) {
        this.supportService = supportService;
        this.clinicService = clinicService;
        this.accountProfile = accountProfile;
    }

    @GetMapping
    @ApiMessage("Fetch all supports")
    public ResponseEntity<ResultPaginationDTO> getAllSupport(
            Pageable pageable) {
        ResultPaginationDTO result = this.supportService.fetchAllSupport(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch support by id")
    public ResponseEntity<ResSupportDTO> getDoctorById(@PathVariable("id") long id) throws IdInvalidException {
        Support support = this.supportService.fetchSupportById(id);

        if (support == null) {
            throw new IdInvalidException("support với id " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.supportService.convertToResSupportDTO(support));
    }

    @PostMapping
    @ApiMessage("Create support")
    public ResponseEntity<ResSupportDTO> postMethodName(@Valid @RequestBody Support support) throws IdInvalidException {
        Account acc = this.supportService.fetchAccountById(support.getAccount().getId());

        if (acc == null) {
            throw new IdInvalidException("Account với id " + support.getAccount().getId() + " không tồn tại");
        }

        this.accountProfile.accountUsed(support.getAccount().getId());
        // check role account
        boolean checkRole = this.accountProfile.accountHasRole(support.getAccount().getId(), RoleName.SUPPORT);
        if (!checkRole) {
            throw new IdInvalidException(
                    "Account id :" + support.getAccount().getId() + " không có quyền " + RoleName.SUPPORT);
        }

        if (this.clinicService.fetchClinicById(support.getClinic().getId()) == null) {
            throw new IdInvalidException("Clinic với id :" + support.getClinic().getId() + " không tồn tại");
        }

        Support supportDB = this.supportService.handleCreateSupport(support);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.supportService.convertToResSupportDTO(supportDB));
    }

    @PutMapping
    @ApiMessage("Update support by id")
    public ResponseEntity<ResSupportDTO> updateDoctor(@Valid @RequestBody Support support) throws IdInvalidException {

        if (this.supportService.fetchSupportById(support.getId()) == null) {
            throw new IdInvalidException("Support với id " + support.getId() + " không tồn tại");
        }

        if (this.clinicService.fetchClinicById(support.getClinic().getId()) == null) {
            throw new IdInvalidException("Clinic với id :" + support.getClinic().getId() + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.supportService.convertToResSupportDTO(this.supportService.handleUpdateSupport(support)));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete doctor by id")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable("id") long id) throws IdInvalidException {
        Support support = this.supportService.fetchSupportById(id);

        if (support == null) {
            throw new IdInvalidException("Doctor với id " + id + " không tồn tại");
        }

        this.supportService.handleDeleteSupport(support);

        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @GetMapping("/search")
    @ApiMessage("Fetch all supports search/filter")
    public ResponseEntity<ResultPaginationDTO> getAllSupportSearch(
            Pageable pageable, SupportCriteriaDTO supportCriteriaDTO) {
        ResultPaginationDTO result = this.supportService.fetchAllSupportSearch(pageable, supportCriteriaDTO);
        return ResponseEntity.ok().body(result);
    }
}
