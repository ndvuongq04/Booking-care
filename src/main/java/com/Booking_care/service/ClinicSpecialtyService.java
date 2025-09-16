package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.ClinicSpecialty;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.ClinicSpecialtyRepository;

@Service
public class ClinicSpecialtyService {
    private final ClinicSpecialtyRepository clinicSpecialtyRepository;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;

    public ClinicSpecialtyService(ClinicSpecialtyRepository clinicSpecialtyRepository,
            ClinicService clinicService,
            SpecialtyService specialtyService) {
        this.clinicSpecialtyRepository = clinicSpecialtyRepository;
        this.clinicService = clinicService;
        this.specialtyService = specialtyService;
    }

    public ClinicSpecialty handleCreateClinicSpecialty(ClinicSpecialty cs) {
        return this.clinicSpecialtyRepository.save(cs);
    }

    public boolean isClinicExits(long id) {
        return this.clinicService.fetchClinicById(id) != null;
    }

    public boolean isSpecialtyExits(long id) {
        return this.specialtyService.fetchSpecialtyById(id) != null;
    }

    public ResultPaginationDTO fetchAllClinicSpecialty(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<ClinicSpecialty> page = this.clinicSpecialtyRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ClinicSpecialty> listCS = page.getContent();

        res.setResult(listCS);
        res.setMeta(meta);

        return res;
    }

    public boolean isClinicSpecialtyExits(Clinic c, Specialty s) {
        return this.clinicSpecialtyRepository.existsByClinicAndSpecialty(c, s);
    }

    public ClinicSpecialty fetchClinicSpecialtyById(long id) {
        return this.clinicSpecialtyRepository.findById(id).orElse(null);
    }

    public void deleteById(long id) {
        this.clinicSpecialtyRepository.deleteById(id);
    }

    public ClinicSpecialty handleUpdateClinicSpecialty(ClinicSpecialty cS) {
        ClinicSpecialty clinicSpecialty = this.fetchClinicSpecialtyById(cS.getId());

        clinicSpecialty.setClinic(cS.getClinic());
        clinicSpecialty.setSpecialty(cS.getSpecialty());

        return this.clinicSpecialtyRepository.save(clinicSpecialty);
    }

    public boolean existsByClinicAndSpecialty(Clinic c, Specialty s, long id) {
        return this.clinicSpecialtyRepository.existsByClinicAndSpecialtyAndIdNot(c, s, id);
    }
}
