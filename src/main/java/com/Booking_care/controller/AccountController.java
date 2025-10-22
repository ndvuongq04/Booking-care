package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.dto.AccountDTO.AccountCriteriaDTO;
import com.Booking_care.domain.dto.AccountDTO.CreateAccountDTO;
import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.AccountDTO.UpdateAccountDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.AccountService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.mapper.account.AccountMapper;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(value = "/accounts")
    @ApiMessage("Create new account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResAccountDTO> createNewAccount(@Valid @RequestBody CreateAccountDTO reqAccount) {
        Account acc = this.accountService.handleCreateAccount(reqAccount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AccountMapper.toResAccountDTO(acc));
    }

    @GetMapping("/accounts/{id}")
    @ApiMessage("Fetch account by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResAccountDTO> getAccountById(@PathVariable("id") long id) {
        Account acc = this.accountService.fetchAccountById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(AccountMapper.toResAccountDTO(acc));
    }

    @PutMapping(value = "/accounts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiMessage("Update a account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResAccountDTO> updateAccount(@Valid @ModelAttribute UpdateAccountDTO reqAcc) {
        Account acc = this.accountService.handleUpdateAccount(reqAcc);
        return ResponseEntity.ok(AccountMapper.toResAccountDTO(acc));
    }

    @DeleteMapping("accounts/{id}")
    @ApiMessage("Delete a account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccountById(@PathVariable("id") long id) {
        this.accountService.handleDeleteAccount(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/accounts")
    @ApiMessage("Fetch all account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> getAllAccount(
            Pageable pageable) {
        ResultPaginationDTO result = this.accountService.fetchAllAccount(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("accounts/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultPaginationDTO> searchAndFilter(
            @Valid @ModelAttribute AccountCriteriaDTO accountCriteriaDTO,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.accountService.getAccountSearch(accountCriteriaDTO, pageable));

    }

}
