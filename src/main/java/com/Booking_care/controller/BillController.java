package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.dto.BillDTO.ReqBillDTO;
import com.Booking_care.domain.dto.BillDTO.ResBillDTO;
import com.Booking_care.service.BillService;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class BillController {
    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @PostMapping("/bill")
    public ResponseEntity<ResBillDTO> createNewBill(@Valid @RequestBody ReqBillDTO reqBill) throws IdInvalidException {
        // TODO: process POST request

        return ResponseEntity.status(HttpStatus.CREATED).body(this.billService.toDto(reqBill));
    }

}
