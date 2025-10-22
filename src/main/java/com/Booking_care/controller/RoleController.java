package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Role;
import com.Booking_care.domain.dto.ResRoleDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.RoleService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.mapper.role.RoleMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create new roles")
    public ResponseEntity<Role> createNewRole(@Valid @RequestBody Role role)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.roleService.handleCreateRole(role));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch roles by id")
    public ResponseEntity<ResRoleDTO> getRoleById(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.fetchRoleById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(RoleMapper.toResRoleDTO(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a roles")
    public ResponseEntity<ResRoleDTO> updateRole(@Valid @RequestBody Role reqRole) throws IdInvalidException {
        return ResponseEntity.ok(RoleMapper.toResRoleDTO(this.roleService.handleUpdateRole(reqRole)));
    }

    @DeleteMapping("roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRoleById(@PathVariable("id") long id)
            throws IdInvalidException {
        this.roleService.handleDeleteRoleById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all role")
    public ResponseEntity<ResultPaginationDTO> getAllRole(
            Pageable pageable) {
        ResultPaginationDTO result = this.roleService.fetchAllRole(pageable);
        return ResponseEntity.ok().body(result);
    }
}
