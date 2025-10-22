package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Booking_care.domain.dto.BillDTO.BillClinicCriteriaDTO;
import com.Booking_care.domain.dto.BillDTO.BillCriteriaDTO;
import com.Booking_care.domain.dto.BillDTO.ReqBillDTO;
import com.Booking_care.domain.dto.BillDTO.ResBillDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.BillService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1")
public class BillController {
    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/bill")
    @ApiMessage("Create new Bill")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ResBillDTO> createNewBill(@Valid @RequestBody ReqBillDTO reqBill) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.billService.createBill(reqBill));
    }

    @GetMapping("/bill")
    @ApiMessage("Fetch all Bill")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllBill(Pageable pageable) {
        return ResponseEntity.ok(this.billService.handleGetAllBill(pageable));
    }

    @GetMapping("/bill/{id}")
    @ApiMessage("Fetch Bill by id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResBillDTO> getBillById(@PathVariable("id") long id) throws IdInvalidException {
        return ResponseEntity.ok(this.billService.getBillById(id));
    }

    @GetMapping("/bill/patient/{id}")
    @ApiMessage("Fetch Bill by patient id")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResultPaginationDTO> getBillByPatientId(@PathVariable("id") long id, Pageable pageable)
            throws IdInvalidException {
        return ResponseEntity.ok(this.billService.getBillByPatientId(id, pageable));
    }

    @GetMapping("/bill/clinic/{id}")
    @ApiMessage("Fetch Bill by clinic id")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ResultPaginationDTO> getBillByClinicId(@PathVariable("id") long id, Pageable pageable)
            throws IdInvalidException {
        return ResponseEntity.ok(this.billService.getBillByClinicId(id, pageable));
    }

    @GetMapping("/bill/clinic/{id}/search")
    @ApiMessage("Search Bill by clinic id")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    public ResponseEntity<ResultPaginationDTO> getBillByClinicIdSearch(@PathVariable("id") long id, Pageable pageable,
            BillClinicCriteriaDTO billClinicCriteriaDTO)
            throws IdInvalidException {
        billClinicCriteriaDTO.setClinicId(id);
        return ResponseEntity.ok(this.billService.getBillByClinicIdSearch(billClinicCriteriaDTO, pageable));
    }

    @GetMapping("/bill/search")
    @ApiMessage("Fetch all Bill search/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllBillSearch(Pageable pageable, BillCriteriaDTO billCriteriaDTO) {
        return ResponseEntity.ok(this.billService.handleGetAllBillSearch(pageable, billCriteriaDTO));
    }

}
