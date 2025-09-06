package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Booking_care.repository.ServiceRepository;
import com.Booking_care.domain.Services;
import com.Booking_care.domain.response.ResServicesDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;

@Service
public class ServicesService {
    private final ServiceRepository serviceRepository;

    public ServicesService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Services handleCreateService(Services reqService) {
        Services service = new Services();
        service.setName(reqService.getName());
        service.setCost(reqService.getCost());
        service.setDescription(reqService.getDescription());

        return this.serviceRepository.save(service);
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
                .map(item -> this.handleConvertToResServicesDTO(item))
                .collect(Collectors.toList());

        res.setResult(listServices);
        res.setMeta(meta);

        return res;
    }

    public ResServicesDTO handleConvertToResServicesDTO(Services services) {
        ResServicesDTO res = new ResServicesDTO();

        res.setId(services.getId());
        res.setName(services.getName());
        res.setDescription(services.getDescription());
        res.setCost(services.getCost());
        res.setCreateAt(services.getCreateAt());
        res.setUpdateAt(services.getUpdateAt());

        return res;
    }

    public Services fetchServicesById(long id) {
        Optional<Services> ser = this.serviceRepository.findById(id);

        if (ser.isPresent()) {
            return ser.get();
        }
        return null;
    }

    public void handleDeleteServices(long id) {
        this.serviceRepository.deleteById(id);
    }

    public Services handleUpdateServices(Services reqServices) {
        Services services = this.fetchServicesById(reqServices.getId());

        if (services != null) {
            services.setName(reqServices.getName());
            services.setCost(reqServices.getCost());
            services.setDescription(reqServices.getDescription());

            this.serviceRepository.save(services);
        }
        return services;
    }

}
