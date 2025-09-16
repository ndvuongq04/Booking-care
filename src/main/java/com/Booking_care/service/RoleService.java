package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Role;
import com.Booking_care.domain.dto.ResRoleDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public boolean isNameExits(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role fetchRoleByName(String name) {
        Optional<Role> role = this.roleRepository.findByName(name);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    public Role handleCreateRole(Role role) {
        return this.roleRepository.save(role);
    }

    public Role fetchRoleById(long id) {
        Optional<Role> role = this.roleRepository.findById(id);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    public ResRoleDTO convertToResRoleDTO(Role role) {
        ResRoleDTO res = new ResRoleDTO();
        res.setId(role.getId());
        res.setName(role.getName());
        res.setDescription(role.getDescription());

        return res;
    }

    public Role handleUpdateRole(Role reqRole) {
        Role role = this.fetchRoleById(reqRole.getId());

        role.setName(reqRole.getName());
        role.setDescription(reqRole.getDescription());
        role = this.roleRepository.save(reqRole);
        return role;
    }

    public void handleDeleteRoleById(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAllRole(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Role> page = this.roleRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResRoleDTO> listRole = page.getContent().stream()
                .map(item -> this.convertToResRoleDTO(item))
                .collect(Collectors.toList());

        res.setResult(listRole);
        res.setMeta(meta);

        return res;
    }

}
