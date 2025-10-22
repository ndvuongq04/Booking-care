package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;
import com.Booking_care.domain.dto.SpecialtyDTO.ResSpecialtyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import com.Booking_care.domain.ClinicSpecialty;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.dto.ClinicSpecialtyDTO.ReqClinicSpecialtyDTO;
import com.Booking_care.domain.dto.ClinicSpecialtyDTO.ResClinicSpecialtyForClinicDTO;
import com.Booking_care.domain.dto.ClinicSpecialtyDTO.ResClinicSpecialtyForSpecialtyDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.ClinicSpecialtyRepository;
import com.Booking_care.repository.SpecialtyRepository;
import com.Booking_care.mapper.specialty.SpecialtyMapper;
import com.Booking_care.mapper.clinic.ClinicMapper;

@Service
public class ClinicSpecialtyService {
    private final ClinicSpecialtyRepository clinicSpecialtyRepository;
    private final ClinicService clinicService;
    private final SpecialtyService specialtyService;
    private final SpecialtyRepository specialtyRepository;

    public ClinicSpecialtyService(ClinicSpecialtyRepository clinicSpecialtyRepository, ClinicService clinicService,
            SpecialtyService specialtyService, SpecialtyRepository specialtyRepository) {
        this.clinicSpecialtyRepository = clinicSpecialtyRepository;
        this.clinicService = clinicService;
        this.specialtyService = specialtyService;
        this.specialtyRepository = specialtyRepository;
    }

    public ClinicSpecialty handleCreateClinicSpecialty(ClinicSpecialty cs) {
        return this.clinicSpecialtyRepository.save(cs);
    }

    public boolean isClinicExits(long id) {
        try {
            this.clinicService.fetchClinicById(id);
            return true;
        } catch (IdInvalidException e) {
            return false;
        }
    }

    public boolean isSpecialtyExits(long id) {
        try {
            this.specialtyService.fetchSpecialtyById(id);
            return true;
        } catch (IdInvalidException e) {
            return false;
        }
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

    public ResultPaginationDTO fetchClinicSpecialtyByClinicId(long clinicId, Pageable pageable)
            throws IdInvalidException {
        // Validation: clinicService sẽ throw exception nếu không tìm thấy
        Clinic clinic = this.clinicService.fetchClinicById(clinicId);
        Page<ClinicSpecialty> page = this.clinicSpecialtyRepository.findByClinic_Id(clinicId, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResClinicSpecialtyForClinicDTO cs = new ResClinicSpecialtyForClinicDTO();
        cs.setClinicId(clinic.getId());
        cs.setClinicName(clinic.getName());

        List<ResSpecialtyDTO> listSpec = page.getContent().stream()
                .map(spec -> SpecialtyMapper.toResSpecialtyDTO(spec.getSpecialty()))
                .collect(Collectors.toList());

        cs.setSpecialties(listSpec);

        res.setMeta(meta);
        res.setResult(cs);

        return res;
    }

    public ResultPaginationDTO fetchClinicSpecialtyBySpecialtyId(long specialtyId, Pageable pageable)
            throws IdInvalidException {
        // Validation: specialtyService sẽ throw exception nếu không tìm thấy
        Specialty specialty = this.specialtyService.fetchSpecialtyById(specialtyId);
        Page<ClinicSpecialty> page = this.clinicSpecialtyRepository.findBySpecialty_Id(specialtyId, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        ResClinicSpecialtyForSpecialtyDTO cs = new ResClinicSpecialtyForSpecialtyDTO();
        cs.setSpecialtyId(specialty.getId());
        cs.setSpecialtyName(specialty.getName());

        List<ResClinicDTO> listSpec = page.getContent().stream()
                .map(spec -> ClinicMapper.toResClinicDTO(spec.getClinic()))
                .collect(Collectors.toList());

        cs.setSpecialties(listSpec);

        res.setMeta(meta);
        res.setResult(cs);

        return res;
    }

    public void deleteById(long id) {
        this.clinicSpecialtyRepository.deleteById(id);
    }

    public boolean existsByClinicAndSpecialty(Clinic c, Specialty s, long id) {
        return this.clinicSpecialtyRepository.existsByClinicAndSpecialtyAndIdNot(c, s, id);
    }

    public void handleAddSpecialtiesForClinic(ReqClinicSpecialtyDTO req) throws IdInvalidException {
        // Validation: clinicService sẽ throw exception nếu không tìm thấy
        Clinic clinic = this.clinicService.fetchClinicById(req.getClinicId());

        try {
            List<Specialty> specialties = this.specialtyRepository.findAllById(req.getSpecialties());

            for (Specialty specialty : specialties) {
                boolean exist = this.clinicSpecialtyRepository.existsByClinicAndSpecialty(clinic, specialty);
                if (!exist) {
                    ClinicSpecialty cs = new ClinicSpecialty();
                    cs.setClinic(clinic);
                    cs.setSpecialty(specialty);
                    this.clinicSpecialtyRepository.save(cs);
                }
            }
        } catch (Exception e) {
            throw new IdInvalidException("Không thể thêm specialties cho clinic: " + e.getMessage());
        }
    }

    @Transactional
    public void handleDeleteClinicSpecialty(long clinicId, long specialtyId) {
        this.clinicSpecialtyRepository.deleteByClinic_IdAndSpecialty_Id(clinicId, specialtyId);
    }
}
