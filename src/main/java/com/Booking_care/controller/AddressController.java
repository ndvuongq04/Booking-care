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

import com.Booking_care.domain.Address;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.AddressService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/address")
    @ApiMessage("Create new Address")
    public ResponseEntity<Address> createNewAddress(
            @Valid @RequestBody Address reqAddress) throws IdInvalidException {
        Address a = this.addressService.handleCreateAddress(reqAddress);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(a);
    }

    @GetMapping("/address/{id}")
    @ApiMessage("Fetch Address by id")
    public ResponseEntity<Address> getAddressById(@PathVariable("id") Long id)
            throws IdInvalidException {
        Address a = this.addressService.fetchAddressById(id);
        return ResponseEntity.ok(a);
    }

    @PutMapping("/address")
    @ApiMessage("Update a Address")
    public ResponseEntity<Address> updateAddress(
            @Valid @RequestBody Address reqAddress)
            throws IdInvalidException {
        Address s = this.addressService.handleUpdateAddress(reqAddress);
        return ResponseEntity.ok(s);
    }

    @DeleteMapping("/address/{id}")
    @ApiMessage("Delete a Address") // set isActive = false
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") Long id)
            throws IdInvalidException {
        this.addressService.handleDeleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/address")
    @ApiMessage("Fetch all address")
    public ResponseEntity<ResultPaginationDTO> getAllAddress(Pageable pageable) {
        ResultPaginationDTO result = this.addressService.fetchAllAddress(pageable);
        return ResponseEntity.ok(result);
    }

}
