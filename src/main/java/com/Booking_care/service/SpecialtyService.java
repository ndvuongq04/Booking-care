package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.dto.ResCloudinaryDTO;
import com.Booking_care.domain.dto.SpecialtyDTO.ReqSpecialtyDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.SpecialtyRepository;
import com.Booking_care.util.error.StorageException;

@Service
public class SpecialtyService {
    private final SpecialtyRepository specialtyRepository;
    private final CloudinaryService cloudinaryService;
    private final String folder = "booking_care/specialty/";

    public SpecialtyService(SpecialtyRepository specialtyRepository,
            CloudinaryService cloudinaryService) {
        this.specialtyRepository = specialtyRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Specialty handleCreateSpecialty(ReqSpecialtyDTO dto) throws StorageException {
        Specialty s = new Specialty();

        s.setName(dto.getName());
        s.setDescription(dto.getDescription());
        Specialty specialtyDb = specialtyRepository.save(s);

        // upload images
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            ResCloudinaryDTO resImg = cloudinaryService.uploadToFolder(dto.getFile(),
                    folder,
                    String.valueOf(specialtyDb.getId()));

            specialtyDb.setImage(resImg.getUrl());
        }

        return specialtyRepository.save(specialtyDb);
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

    public Specialty handleUpdateSpecialty(ReqSpecialtyDTO dto, long id) throws StorageException {
        Specialty s = this.specialtyRepository.findById(id).orElse(null);
        if (s != null) {
            s.setName(dto.getName());
            s.setDescription(dto.getDescription());
            s.setIsActive(dto.getIsActive());

            // upload images
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                ResCloudinaryDTO resImg = cloudinaryService.uploadToFolder(dto.getFile(),
                        folder,
                        String.valueOf(s.getId()));

                s.setImage(resImg.getUrl());
            }

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
