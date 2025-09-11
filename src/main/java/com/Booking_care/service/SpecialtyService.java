package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.SpecialtyRepository;

@Service
public class SpecialtyService {
    private final SpecialtyRepository specialtyRepository;

    public SpecialtyService(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    public Specialty handleCreateSpecialty(Specialty dto) {
        Specialty s = new Specialty();

        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        s.setImage(dto.getImage());
        return specialtyRepository.save(s);
    }

    public boolean isNameExits(String name) {
        return this.specialtyRepository.existsByName(name);
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        return specialtyRepository.existsByNameAndIdNot(name, id);
    }

    public Specialty fetchSpecialtyById(Long id) {
        return specialtyRepository.findById(id).orElse(null);
    }

    public Specialty handleUpdateSpecialty(Specialty dto) {
        Specialty s = this.specialtyRepository.findById(dto.getId()).orElse(null);
        if (s != null) {
            s.setName(dto.getName());
            s.setDescription(dto.getDescription());
            s.setImage(dto.getImage());
            s.setIsActive(dto.getIsActive());

            this.specialtyRepository.save(s);
        }
        return s;
    }

    public void handleDeleteSpecialty(Long id) {
        Specialty s = this.fetchSpecialtyById(id);
        s.setIsActive(false);
        this.specialtyRepository.save(s);
    }

    public ResultPaginationDTO fetchAllSpecialty(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Specialty> page = this.specialtyRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<Specialty> listSpecialty = page.getContent();

        res.setResult(listSpecialty);
        res.setMeta(meta);

        return res;
    }
}
