package com.Booking_care.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.dto.ClinicDTO.ClinicCriteriaDTO;
import com.Booking_care.domain.dto.ClinicDTO.ReqClinicDTO;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.ClinicService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.util.error.StorageException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ClinicController {
    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PostMapping(value = "/clinics")
    @ApiMessage("Create new clinic")
    public ResponseEntity<ResClinicDTO> createNewClinic(@Valid @RequestBody ReqClinicDTO reqClinic)
            throws IdInvalidException {
        boolean isNameExits = this.clinicService.isNameExits(reqClinic.getName());

        if (isNameExits) {
            throw new IdInvalidException(
                    "Name " + reqClinic.getName() + " đã tồn tại, Vui lòng sử dụng name khác.");
        }

        boolean isAddressActiveExits = this.clinicService.existsAddressActiveById(reqClinic.getAddressId());
        if (!isAddressActiveExits) {
            throw new IdInvalidException(
                    "Address : " + reqClinic.getAddressId()
                            + " không tồn tại (Không hoạt động), Vui lòng sử dụng address khác.");
        }
        Clinic c = this.clinicService.handleCreateClinic(reqClinic);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.clinicService.convertToClinicDTO(c));
    }

    @GetMapping("/clinics/{id}")
    @ApiMessage("Fetch clinic by id")
    public ResponseEntity<ResClinicDTO> getClinicById(@PathVariable("id") long id) throws IdInvalidException {
        Clinic c = this.clinicService.fetchClinicById(id);

        if (c == null) {
            throw new IdInvalidException("Clinic với id " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.clinicService.convertToClinicDTO(c));
    }

    @DeleteMapping("clinics/{id}")
    @ApiMessage("Delete a clinic")
    public ResponseEntity<Void> deleteClinicById(@PathVariable("id") long id) throws IdInvalidException {
        Clinic c = this.clinicService.fetchClinicById(id);

        if (c == null) {
            throw new IdInvalidException("Clinic với id " + id + " không tồn tại");
        }
        this.clinicService.handleDeleteClinic(id);

        return ResponseEntity.ok(null);
    }

    @PutMapping(value = "/clinics/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Update a clinic")
    public ResponseEntity<ResClinicDTO> updateAccount(@Valid @ModelAttribute ReqClinicDTO reqClinic,
            @PathVariable("id") long id,
            @RequestParam(value = "file", required = false) MultipartFile file)
            throws IdInvalidException, StorageException {
        Clinic c = this.clinicService.fetchClinicById(id);

        if (c == null) {
            throw new IdInvalidException("Clinic với id " + id + " không tồn tại");
        }

        boolean isNameExits = this.clinicService.existsByNameAndIdNot(reqClinic.getName(), id);
        if (isNameExits) {
            throw new IdInvalidException(
                    "Name " + reqClinic.getName() + " đã tồn tại, Vui lòng sử dụng name khác.");
        }

        boolean isAddressExist = this.clinicService.existsAddressActiveById(reqClinic.getAddressId());
        if (!isAddressExist) {
            throw new IdInvalidException(
                    "Address : " + reqClinic.getAddressId()
                            + " không tồn tại, Vui lòng sử dụng address khác.");
        }
        Clinic clinic = this.clinicService.handleUpdateClinic(reqClinic, id, file);

        return ResponseEntity.ok(this.clinicService.convertToClinicDTO(clinic));
    }

    @GetMapping("/clinics")
    @ApiMessage("Fetch all clinic")
    public ResponseEntity<ResultPaginationDTO> getAllClinic(
            Pageable pageable) {
        ResultPaginationDTO result = this.clinicService.fetchAllClinic(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/clinics/search")
    @ApiMessage("Fetch all clinic search/filter")
    public ResponseEntity<ResultPaginationDTO> getAllClinicSearch(
            Pageable pageable, ClinicCriteriaDTO clinicCriteriaDTO) {
        ResultPaginationDTO result = this.clinicService.fetchAllClinicSearch(pageable, clinicCriteriaDTO);
        return ResponseEntity.ok().body(result);
    }
}
