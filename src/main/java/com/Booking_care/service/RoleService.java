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
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.role.RoleMapper;

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
        // Validation: check name exists
        if (this.roleRepository.existsByName(role.getName())) {
            throw new IdInvalidException(
                    "Role với name " + role.getName() + " đã tồn tại.");
        }

        try {
            return this.roleRepository.save(role);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể tạo role: " + e.getMessage());
        }
    }

    public Role fetchRoleById(long id) {
        return this.roleRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Role với id " + id + " không tồn tại"));
    }


    public Role handleUpdateRole(Role reqRole) {
        // Validation: check role exists (will throw if not found)
        Role role = this.fetchRoleById(reqRole.getId());

        // Validation: check name exists for other roles
        Role checkName = this.fetchRoleByName(reqRole.getName());
        if (checkName != null && checkName.getId() != reqRole.getId()) {
            throw new IdInvalidException("Role với name " + reqRole.getName() + " đã tồn tại.");
        }

        try {
            role.setName(reqRole.getName());
            role.setDescription(reqRole.getDescription());
            return this.roleRepository.save(reqRole);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể cập nhật role: " + e.getMessage());
        }
    }

    public void handleDeleteRoleById(long id) {
        // Validation: check role exists (will throw if not found)
        this.fetchRoleById(id);
        
        try {
            this.roleRepository.deleteById(id);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa role: " + e.getMessage());
        }
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
                .map(RoleMapper::toResRoleDTO)
                .collect(Collectors.toList());

        res.setResult(listRole);
        res.setMeta(meta);

        return res;
    }

}
