package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Address;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.ClinicRepository;

@Service
public class ClinicService {
    private final ClinicRepository clinicRepository;
    private final AddressService addressService;

    public ClinicService(ClinicRepository clinicRepository,
            AddressService addressService) {
        this.clinicRepository = clinicRepository;
        this.addressService = addressService;
    }

    public boolean isNameExits(String name) {
        return this.clinicRepository.existsByName(name);
    }

    public Clinic handleCreateClinic(Clinic c) {
        return this.clinicRepository.save(c);
    }

    public ResultPaginationDTO fetchAllClinic(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Clinic> page = this.clinicRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResClinicDTO> listClinic = page.getContent().stream()
                .map(item -> this.convertToClinicDTO(item))
                .collect(Collectors.toList());

        res.setResult(listClinic);
        res.setMeta(meta);

        return res;
    }

    public Clinic fetchClinicById(long id) {
        return this.clinicRepository.findById(id).orElse(null);
    }

    public void handleDeleteClinic(long id) {
        Clinic c = this.fetchClinicById(id);
        if (c != null) {
            c.setIsActive(false);
            this.clinicRepository.save(c);
        }
    }

    public Clinic handleUpdateClinic(Clinic clinic) {
        Clinic c = this.fetchClinicById(clinic.getId());
        if (c != null) {
            c.setName(clinic.getName());
            c.setPhoneNumber(clinic.getPhoneNumber());
            c.setPosition(clinic.getPosition());
            c.setIsActive(clinic.getIsActive());
            c.setImage(clinic.getImage());

            if (clinic.getAddress() != null) {
                Address a = this.addressService.fetchAddressById(clinic.getAddress().getId());
                c.setAddress(a);
            }

            this.clinicRepository.save(c);
        }
        return c;
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        return clinicRepository.existsByNameAndIdNot(name, id);
    }

    public boolean existsAddressActiveById(long id) {
        // address active true
        Address a = this.addressService.fetchAddressById(id);
        return a != null;
    }

    public ResClinicDTO convertToClinicDTO(Clinic clinic) {
        if (clinic == null)
            return null;

        ResClinicDTO dto = new ResClinicDTO();
        dto.setId(clinic.getId());
        dto.setName(clinic.getName());
        dto.setDescription(clinic.getDescription());
        dto.setPosition(clinic.getPosition());
        dto.setPhoneNumber(clinic.getPhoneNumber());
        dto.setImage(clinic.getImage());

        if (clinic.getAddress() != null) {
            dto.setAddress(new ResClinicDTO.ResAddressDTO(
                    clinic.getAddress().getId(),
                    clinic.getAddress().getCity()));
        }

        return dto;
    }

}