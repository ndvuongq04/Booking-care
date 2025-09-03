package com.Booking_care.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Booking_care.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String name);

    Optional<Role> findByName(String name);
}
