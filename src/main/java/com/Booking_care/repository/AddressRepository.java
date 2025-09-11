package com.Booking_care.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    boolean existsByCityAndIdNot(String name, Long id);

    boolean existsByCity(String name);

}
