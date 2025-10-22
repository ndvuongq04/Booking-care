package com.Booking_care.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.dto.SpecialtyDTO.ResSpecialtyDTO;
import com.Booking_care.domain.Specialty;
import com.Booking_care.domain.dto.ResCloudinaryDTO;
import com.Booking_care.domain.dto.SpecialtyDTO.ReqSpecialtyDTO;
import com.Booking_care.domain.dto.SpecialtyDTO.SpecialtyCriteriaDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.SpecialtyRepository;
import com.Booking_care.service.specification.SpecialtySpecs;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.util.error.StorageException;
import com.Booking_care.mapper.specialty.SpecialtyMapper;

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
        // Validation: check name exists
        if (this.specialtyRepository.existsByName(dto.getName())) {
            throw new IdInvalidException(
                    "Tên chuyên khoa '" + dto.getName() + "' đã tồn tại, vui lòng chọn tên khác");
        }

        try {
            Specialty s = new Specialty();

            s.setName(dto.getName());
            s.setDescription(dto.getDescription());
            Specialty specialtyDb = specialtyRepository.save(s);

            // upload images
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                try {
                    ResCloudinaryDTO resImg = cloudinaryService.uploadToFolder(dto.getFile(),
                            folder,
                            String.valueOf(specialtyDb.getId()));
                    specialtyDb.setImage(resImg.getUrl());
                } catch (Exception e) {
                    throw new StorageException("Không thể upload ảnh: " + e.getMessage());
                }
            }

            return specialtyRepository.save(specialtyDb);
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo specialty: " + e.getMessage());
        }
    }

    public boolean isNameExits(String name) {
        return this.specialtyRepository.existsByName(name);
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        return specialtyRepository.existsByNameAndIdNot(name, id);
    }

    public Specialty fetchSpecialtyById(Long id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Specialty với id " + id + " không tồn tại"));
    }

    public Specialty handleUpdateSpecialty(ReqSpecialtyDTO dto, long id) throws StorageException {
        // Validation: check specialty exists (will throw if not found)
        Specialty s = this.fetchSpecialtyById(id);

        // Validation: check name exists for other specialties
        if (this.specialtyRepository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new IdInvalidException(
                    "Tên chuyên khoa '" + dto.getName() + "' đã tồn tại, vui lòng chọn tên khác");
        }

        try {
            s.setName(dto.getName());
            s.setDescription(dto.getDescription());
            s.setIsActive(dto.getIsActive());

            // upload images
            if (dto.getFile() != null && !dto.getFile().isEmpty()) {
                try {
                    ResCloudinaryDTO resImg = cloudinaryService.uploadToFolder(dto.getFile(),
                            folder,
                            String.valueOf(s.getId()));
                    s.setImage(resImg.getUrl());
                } catch (Exception e) {
                    throw new StorageException("Không thể upload ảnh: " + e.getMessage());
                }
            }

            return this.specialtyRepository.save(s);
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật specialty: " + e.getMessage());
        }
    }

    public void handleDeleteSpecialty(Long id) {
        // Validation: check specialty exists (will throw if not found)
        Specialty s = this.fetchSpecialtyById(id);
        
        try {
            s.setIsActive(false);
            this.specialtyRepository.save(s);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa specialty: " + e.getMessage());
        }
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

    public Page<Specialty> getAllWithSpecs(Pageable pageable, SpecialtyCriteriaDTO specialtyCriteriaDTO) {
        Specification<Specialty> combinedSpec = Specification.where(null);

        if (specialtyCriteriaDTO.getName() != null && !specialtyCriteriaDTO.getName().trim().isEmpty()) {
            Specification<Specialty> currentSpec = SpecialtySpecs.nameLikeIgnoreCase(specialtyCriteriaDTO.getName());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (specialtyCriteriaDTO.getMonthYear() != null) {
            YearMonth monthYear = specialtyCriteriaDTO.getMonthYear();

            Instant from = monthYear.atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng
                    .toInstant();

            Instant to = monthYear.plusMonths(1).atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng kế tiếp
                    .toInstant();

            Specification<Specialty> currentSpec = SpecialtySpecs.dateBetween(from, to);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.specialtyRepository.findAll(combinedSpec, pageable);
    }

    public ResultPaginationDTO fetchAllSpecialtySearch(Pageable pageable, SpecialtyCriteriaDTO specialtyCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Specialty> page = this.getAllWithSpecs(pageable, specialtyCriteriaDTO);

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
