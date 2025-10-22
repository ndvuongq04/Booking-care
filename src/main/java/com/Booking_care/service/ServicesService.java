package com.Booking_care.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.Booking_care.repository.ServiceRepository;
import com.Booking_care.service.specification.ServicesSpecs;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.domain.Services;
import com.Booking_care.mapper.services.ServicesMapper;
import com.Booking_care.domain.dto.ServicesDTO.ResServicesDTO;
import com.Booking_care.domain.dto.ServicesDTO.ServicesCriteriaDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;

@Service
public class ServicesService {
    private final ServiceRepository serviceRepository;

    public ServicesService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Services handleCreateService(Services reqService) {
        // Validation: check name exists
        if (this.serviceRepository.existsByName(reqService.getName())) {
            throw new IdInvalidException(
                    "Tên Dịch vụ'" + reqService.getName() + "' đã tồn tại, vui lòng chọn tên khác");
        }

        try {
            Services service = new Services();
            service.setName(reqService.getName());
            service.setCost(reqService.getCost());
            service.setDescription(reqService.getDescription());

            return this.serviceRepository.save(service);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo service: " + e.getMessage());
        }
    }

    public ResultPaginationDTO fetchAllServices(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Services> page = this.serviceRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        List<ResServicesDTO> listServices = page.getContent().stream()
                .map(ServicesMapper::toResServicesDTO)
                .collect(Collectors.toList());

        res.setResult(listServices);
        res.setMeta(meta);

        return res;
    }

    public Services fetchServicesById(long id) {
        return this.serviceRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Services với id " + id + " không tồn tại"));
    }

    public void handleDeleteServices(long id) {
        // Validation: check services exists (will throw if not found)
        this.fetchServicesById(id);

        try {
            this.serviceRepository.deleteById(id);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa service: " + e.getMessage());
        }
    }

    public Services handleUpdateServices(Services reqServices) {
        // Validation: check services exists (will throw if not found)
        Services services = this.fetchServicesById(reqServices.getId());

        try {
            services.setName(reqServices.getName());
            services.setCost(reqServices.getCost());
            services.setDescription(reqServices.getDescription());

            return this.serviceRepository.save(services);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật service: " + e.getMessage());
        }
    }

    public boolean isNameExits(String name) {
        return this.serviceRepository.existsByName(name);
    }

    public Page<Services> getAllWithSpecs(Pageable pageable, ServicesCriteriaDTO servicesCriteriaDTO) {
        Specification<Services> combinedSpec = Specification.where(null);

        if (servicesCriteriaDTO.getName() != null && !servicesCriteriaDTO.getName().trim().isEmpty()) {
            Specification<Services> currentSpec = ServicesSpecs.nameLikeIgnoreCase(servicesCriteriaDTO.getName());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        if (servicesCriteriaDTO.getCost() != null) {
            Specification<Services> currentSpec = ServicesSpecs.costBetween(servicesCriteriaDTO.getCost().getMin(),
                    servicesCriteriaDTO.getCost().getMax());
            combinedSpec = combinedSpec.and(currentSpec);
        }

        return this.serviceRepository.findAll(combinedSpec, pageable);
    }

    public ResultPaginationDTO fetchAllServicesSearch(Pageable pageable, ServicesCriteriaDTO servicesCriteriaDTO) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Services> page = this.getAllWithSpecs(pageable, servicesCriteriaDTO);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        List<ResServicesDTO> listServices = page.getContent().stream()
                .map(ServicesMapper::toResServicesDTO)
                .collect(Collectors.toList());

        res.setResult(listServices);
        res.setMeta(meta);

        return res;
    }

}
