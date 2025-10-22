package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Services;
import com.Booking_care.domain.dto.ServicesDTO.ResServicesDTO;
import com.Booking_care.domain.dto.ServicesDTO.ServicesCriteriaDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.ServicesService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.services.ServicesMapper;
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
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1")
public class ServiceController {
    private final ServicesService servicesService;

    public ServiceController(ServicesService servicesService) {
        this.servicesService = servicesService;
    }

    @PostMapping("/services")
    @ApiMessage("Create new Service")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResServicesDTO> createNewService(@Valid @RequestBody Services reqService)
            throws IdInvalidException {
        Services services = this.servicesService.handleCreateService(reqService);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ServicesMapper.toResServicesDTO(services));
    }

    @GetMapping("/services/{id}")
    @ApiMessage("Fetch services by id")
    public ResponseEntity<ResServicesDTO> getServicesById(@PathVariable("id") long id) throws IdInvalidException {
        Services services = this.servicesService.fetchServicesById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ServicesMapper.toResServicesDTO(services));
    }

    @PutMapping("/services")
    @ApiMessage("Update a services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResServicesDTO> updateServices(@Valid @RequestBody Services reqService)
            throws IdInvalidException {
        Services services = this.servicesService.handleUpdateServices(reqService);
        return ResponseEntity.ok(ServicesMapper.toResServicesDTO(services));
    }

    @DeleteMapping("services/{id}")
    @ApiMessage("Delete a services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteServicesById(@PathVariable("id") long id)
            throws IdInvalidException {
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

    @GetMapping("/services/search")
    @ApiMessage("Fetch all services search/filter")
    public ResponseEntity<ResultPaginationDTO> getAllServicesSearch(
            Pageable pageable, ServicesCriteriaDTO servicesCriteriaDTO) {
        ResultPaginationDTO result = this.servicesService.fetchAllServicesSearch(pageable, servicesCriteriaDTO);
        return ResponseEntity.ok().body(result);
    }

}