package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Services;
import com.Booking_care.domain.response.ResServicesDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.ServicesService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class ServiceController {
    private final ServicesService servicesService;

    public ServiceController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @PostMapping("/services")
    @ApiMessage("Create new Service")
    public ResponseEntity<ResServicesDTO> createNewService(@Valid @RequestBody Services reqService)
            throws IdInvalidException {

        Services services = this.servicesService.handleCreateService(reqService);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.servicesService.handleConvertToResServicesDTO(services));
    }

    @GetMapping("/services/{id}")
    @ApiMessage("Fetch services by id")
    public ResponseEntity<ResServicesDTO> getServicesById(@PathVariable("id") long id) throws IdInvalidException {
        Services services = this.servicesService.fetchServicesById(id);

        if (services == null) {
            throw new IdInvalidException("Services với id " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.servicesService.handleConvertToResServicesDTO(services));
    }

    @PutMapping("/services")
    @ApiMessage("Update a services")
    public ResponseEntity<ResServicesDTO> updateServices(@Valid @RequestBody Services reqService)
            throws IdInvalidException {
        Services services = this.servicesService.handleUpdateServices(reqService);

        if (services == null) {
            throw new IdInvalidException("Account với id " + reqService.getId() + " không tồn tại");
        }

        return ResponseEntity.ok(this.servicesService.handleConvertToResServicesDTO(services));
    }

    @DeleteMapping("services/{id}")
    @ApiMessage("Delete a services")
    public ResponseEntity<Void> deleteServicesById(@PathVariable("id") long id)
            throws IdInvalidException {
        Services services = this.servicesService.fetchServicesById(id);

        if (services == null) {
            throw new IdInvalidException("Account với id " + id + " không tồn tại");
        }
        this.servicesService.handleDeleteServices(id);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/services")
    @ApiMessage("Fetch all services")
    public ResponseEntity<ResultPaginationDTO> getAllServices(
            Pageable pageable) {
        ResultPaginationDTO result = this.servicesService.fetchAllServices(pageable);
        return ResponseEntity.ok().body(result);
    }

}