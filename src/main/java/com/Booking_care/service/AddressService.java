package com.Booking_care.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Address;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.AddressRepository;
import com.Booking_care.util.error.IdInvalidException;

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
        // Validation: check city exists
        if (this.addressRepository.existsByCity(address.getCity())) {
            throw new IdInvalidException(
                    "Thành phố '" + address.getCity() + "' đã tồn tại, vui lòng chọn tên khác");
        }

        try {
            return this.addressRepository.save(address);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo address: " + e.getMessage());
        }
    }

    public Address fetchAddressById(long id) {
        return this.addressRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Address với id " + id + " không tồn tại"));
    }

    public boolean isCityExits(String city) {
        return this.addressRepository.existsByCity(city);
    }

    public Address handleUpdateAddress(Address a) {
        // Validation: check address exists (will throw if not found)
        Address address = this.fetchAddressById(a.getId());

        // Validation: check city exists for other addresses
        if (this.addressRepository.existsByCityAndIdNot(a.getCity(), a.getId())) {
            throw new IdInvalidException(
                    "Tên chuyên khoa '" + a.getCity() + "' đã tồn tại, vui lòng chọn tên khác");
        }

        try {
            address.setCity(a.getCity());
            address.setIsActive(a.getIsActive());
            return this.addressRepository.save(address);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật address: " + e.getMessage());
        }
    }

    public void handleDeleteAddress(long id) {
        // Validation: check address exists (will throw if not found)
        Address a = this.fetchAddressById(id);
        
        try {
            a.setIsActive(false);
            this.addressRepository.save(a);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa address: " + e.getMessage());
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
