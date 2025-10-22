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
import com.Booking_care.domain.Support;
import com.Booking_care.domain.dto.SupportDTO.ResSupportDTO;
import com.Booking_care.domain.dto.SupportDTO.SupportCriteriaDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.SupportService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.support.SupportMapper;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/supports")
public class SupportController {
    private final SupportService supportService;

    public SupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @GetMapping("/{supportId}/clinic")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResClinicDTO> getClinicBySupportId(@PathVariable Long supportId) {
        return ResponseEntity.ok(supportService.getClinicBySupportId(supportId));
    }

    @GetMapping
    @ApiMessage("Fetch all supports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllSupport(
            Pageable pageable) {
        ResultPaginationDTO result = this.supportService.fetchAllSupport(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/{id}")
    @ApiMessage("Fetch support by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ResSupportDTO> getDoctorById(@PathVariable("id") long id) throws IdInvalidException {
        Support support = this.supportService.fetchSupportById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(SupportMapper.toResSupportDTO(support));
    }

    @PostMapping
    @ApiMessage("Create support")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResSupportDTO> postMethodName(@Valid @RequestBody Support support) throws IdInvalidException {
        Support supportDB = this.supportService.handleCreateSupport(support);
        return ResponseEntity.status(HttpStatus.CREATED).body(SupportMapper.toResSupportDTO(supportDB));
    }

    @PutMapping
    @ApiMessage("Update support by id")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ResSupportDTO> updateDoctor(@Valid @RequestBody Support support) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SupportMapper.toResSupportDTO(this.supportService.handleUpdateSupport(support)));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete doctor by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDoctorById(@PathVariable("id") long id) throws IdInvalidException {
        this.supportService.handleDeleteSupport(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(null);
    }

    @GetMapping("/search")
    @ApiMessage("Fetch all supports search/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllSupportSearch(
            Pageable pageable, SupportCriteriaDTO supportCriteriaDTO) {
        ResultPaginationDTO result = this.supportService.fetchAllSupportSearch(pageable, supportCriteriaDTO);
        return ResponseEntity.ok().body(result);
    }
}
