package com.Booking_care.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Address;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.AddressRepository;

@Service
public class AddressService {
    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public boolean existsByCityAndIdNot(String city, long id) {
        return this.addressRepository.existsByCityAndIdNot(city, id);
    }

    public Address handleCreateAddress(Address address) {
        return this.addressRepository.save(address);
    }

    public Address fetchAddressById(long id) {
        return this.addressRepository.findById(id).orElse(null);
    }

    public boolean isCityExits(String city) {
        return this.addressRepository.existsByCity(city);
    }

    public Address handleUpdateAddress(Address a) {
        Address address = this.fetchAddressById(a.getId());
        if (address != null) {
            address.setCity(a.getCity());
            address.setIsActive(a.getIsActive());
            this.addressRepository.save(address);
        }

        return address;
    }

    public void handleDeleteAddress(long id) {
        Address a = this.fetchAddressById(id);
        if (a != null) {
            a.setIsActive(false);
            this.addressRepository.save(a);
        }
    }

    public ResultPaginationDTO fetchAllAddress(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Address> page = this.addressRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<Address> listAddress = page.getContent();

        res.setResult(listAddress);
        res.setMeta(meta);

        return res;
    }

}
