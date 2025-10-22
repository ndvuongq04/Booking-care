package com.Booking_care.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.Booking_care.domain.Address;
import com.Booking_care.domain.Clinic;
import com.Booking_care.domain.dto.ResCloudinaryDTO;
import com.Booking_care.domain.dto.ClinicDTO.ClinicCriteriaDTO;
import com.Booking_care.domain.dto.ClinicDTO.ReqClinicDTO;
import com.Booking_care.domain.dto.ClinicDTO.ResClinicDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.ClinicRepository;
import com.Booking_care.service.specification.ClinicSpecs;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.util.error.StorageException;
import com.Booking_care.mapper.clinic.ClinicMapper;

@Service
public class ClinicService {
    private final ClinicRepository clinicRepository;
    private final AddressService addressService;
    private final CloudinaryService cloudinaryService;
    private final String folder = "booking_care/clinic/";

    public ClinicService(ClinicRepository clinicRepository,
            AddressService addressService,
            CloudinaryService cloudinaryService) {
        this.clinicRepository = clinicRepository;
        this.addressService = addressService;
        this.cloudinaryService = cloudinaryService;
    }

    public boolean isNameExits(String name) {
        return this.clinicRepository.existsByName(name);
    }

    public Clinic handleCreateClinic(ReqClinicDTO c) {
        // Validation: check name exists
        if (this.clinicRepository.existsByName(c.getName())) {
            throw new IdInvalidException(
                    "Name " + c.getName() + " đã tồn tại, Vui lòng sử dụng name khác.");
        }

        // Validation: check address exists (addressService will throw if not found)
        Address address = this.addressService.fetchAddressById(c.getAddressId());

        try {
            Clinic clinic = new Clinic();

            // if (c.getFile() != null && !c.getFile().isEmpty()) {
            // ResCloudinaryDTO resImg = cloudinaryService.uploadToFolder(c.getFile(),
            // folder,
            // c.getName());
            // clinic.setImage(resImg.getUrl());

            // }

            clinic.setName(c.getName());
            clinic.setDescription(c.getDescription());
            clinic.setPosition(c.getPosition());
            clinic.setPhoneNumber(c.getPhoneNumber());

            //
            Address a = new Address();
            a.setId(c.getAddressId());
            clinic.setAddress(a);

            return this.clinicRepository.save(clinic);
        } catch (Exception e) {
            throw new StorageException("Không thể tạo clinic: " + e.getMessage(), e);
        }
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
                .map(ClinicMapper::toResClinicDTO)
                .collect(Collectors.toList());

        res.setResult(listClinic);
        res.setMeta(meta);

        return res;
    }

    public Clinic fetchClinicById(long id) {
        return this.clinicRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Clinic với id " + id + " không tồn tại"));
    }

    public void handleDeleteClinic(long id) {
        // Validation: check clinic exists (will throw if not found)
        Clinic c = this.fetchClinicById(id);

        try {
            c.setIsActive(false);
            this.clinicRepository.save(c);
        } catch (Exception e) {
            throw new StorageException("Không thể xóa clinic: " + e.getMessage(), e);
        }
    }

    public Clinic handleUpdateClinic(ReqClinicDTO clinic, long id, MultipartFile file) throws StorageException {
        // Validation: check clinic exists (will throw if not found)
        Clinic c = this.fetchClinicById(id);

        // Validation: check name exists for other clinics
        if (this.clinicRepository.existsByNameAndIdNot(clinic.getName(), id)) {
            throw new IdInvalidException(
                    "Name " + clinic.getName() + " đã tồn tại, Vui lòng sử dụng name khác.");
        }

        // Validation: check address exists (addressService will throw if not found)
        Address address = this.addressService.fetchAddressById(clinic.getAddressId());

        try {
            c.setName(clinic.getName());
            c.setPhoneNumber(clinic.getPhoneNumber());
            c.setPosition(clinic.getPosition());
            c.setDescription(clinic.getDescription());
            c.setIsActive(clinic.getIsActive());

            // upload image
            if (file != null && !file.isEmpty()) {
                try {
                    ResCloudinaryDTO resImg = cloudinaryService.uploadToFolder(file, folder,
                            String.valueOf(c.getId()));
                    c.setImage(resImg.getUrl());
                } catch (Exception e) {
                    throw new StorageException("Không thể upload ảnh: " + e.getMessage(), e);
                }
            }

            if (clinic.getAddressId() != null) {
                Address a = this.addressService.fetchAddressById(clinic.getAddressId());
                c.setAddress(a);
            }

            return this.clinicRepository.save(c);
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Không thể cập nhật clinic: " + e.getMessage(), e);
        }
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        return clinicRepository.existsByNameAndIdNot(name, id);
    }

    public boolean existsAddressActiveById(long id) {
        try {
            Address a = this.addressService.fetchAddressById(id);
            return a != null && a.getIsActive();
        } catch (IdInvalidException e) {
            return false;
        }
    }

    public Page<Clinic> getAllClinicWithSpecs(Pageable pageable, ClinicCriteriaDTO clinicCriteriaDTO) {
        Specification<Clinic> combinedSpec = Specification.where(null);

        if (clinicCriteriaDTO.getName() != null && !clinicCriteriaDTO.getName().trim().isEmpty()) {
            Specification<Clinic> currentSpec = ClinicSpecs.nameLikeIgnoreCase(clinicCriteriaDTO.getName());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (clinicCriteriaDTO.getAddressId() != null) {
            Specification<Clinic> currentSpec = ClinicSpecs.addressJointEqual(clinicCriteriaDTO.getAddressId());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (clinicCriteriaDTO.getPhoneNumber() != null && !clinicCriteriaDTO.getPhoneNumber().trim().isEmpty()) {
            Specification<Clinic> currentSpec = ClinicSpecs
                    .phoneNumberLikeIgnoreCase(clinicCriteriaDTO.getPhoneNumber());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (clinicCriteriaDTO.getMonthYear() != null) {
            YearMonth monthYear = clinicCriteriaDTO.getMonthYear();

            Instant from = monthYear.atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng
                    .toInstant();

            Instant to = monthYear.plusMonths(1).atDay(1)
                    .atStartOfDay(ZoneOffset.UTC) // mốc 00:00 ngày đầu tháng kế tiếp
                    .toInstant();

            Specification<Clinic> currentSpec = ClinicSpecs.dateBetween(from, to);
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.clinicRepository.findAll(combinedSpec, pageable);
    }

    public ResultPaginationDTO fetchAllClinicSearch(Pageable pageable, ClinicCriteriaDTO clinicCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Clinic> page = this.getAllClinicWithSpecs(pageable, clinicCriteriaDTO);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResClinicDTO> listClinic = page.getContent().stream()
                .map(ClinicMapper::toResClinicDTO)
                .collect(Collectors.toList());

        res.setResult(listClinic);
        res.setMeta(meta);

        return res;
    }

}