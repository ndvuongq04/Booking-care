package com.Booking_care.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.Role;
import com.Booking_care.domain.request.accountDTO.AccountCriteriaDTO;
import com.Booking_care.domain.request.accountDTO.CreateAccountDTO;
import com.Booking_care.domain.request.accountDTO.UpdateAccountDTO;
import com.Booking_care.domain.response.ResAccountDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.service.AccountService;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/accounts")
    @ApiMessage("Create new account")
    public ResponseEntity<ResAccountDTO> createNewAccount(@Valid @RequestBody CreateAccountDTO reqAccount)
            throws IdInvalidException {
        boolean isEmailExits = this.accountService.isEmailExits(reqAccount.getEmail());

        if (isEmailExits) {
            throw new IdInvalidException(
                    "Email " + reqAccount.getEmail() + " đã tồn tại, Vui lòng sử dụng email khác.");
        }

        Account acc = this.accountService.handleCreateAccount(reqAccount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.accountService.convertToResAccountDTO(acc));
    }

    @GetMapping("/accounts/{id}")
    @ApiMessage("Fetch account by id")
    public ResponseEntity<ResAccountDTO> getAccountById(@PathVariable("id") long id) throws IdInvalidException {
        Account acc = this.accountService.fetchAccountById(id);

        if (acc == null) {
            throw new IdInvalidException("Account với id " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.accountService.convertToResAccountDTO(acc));
    }

    @PutMapping("/accounts")
    @ApiMessage("Update a account")
    public ResponseEntity<ResAccountDTO> updateAccount(@Valid @RequestBody UpdateAccountDTO reqAcc)
            throws IdInvalidException {
        Account acc = this.accountService.handleUpdateAccount(reqAcc);

        if (acc == null) {
            throw new IdInvalidException("Account với id " + reqAcc.getId() + " không tồn tại");
        }

        return ResponseEntity.ok(this.accountService.convertToResAccountDTO(acc));
    }

    @DeleteMapping("accounts/{id}")
    @ApiMessage("Delete a account")
    public ResponseEntity<Void> deleteAccountById(@PathVariable("id") long id) throws IdInvalidException {
        Account acc = this.accountService.fetchAccountById(id);

        if (acc == null) {
            throw new IdInvalidException("Account với id " + id + " không tồn tại");
        }
        this.accountService.handleDeleteAccount(id);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/accounts")
    @ApiMessage("Fetch all account")
    public ResponseEntity<ResultPaginationDTO> getAllAccount(
            Pageable pageable) {
        ResultPaginationDTO result = this.accountService.fetchAllAccount(pageable);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("accounts/search")
    public ResponseEntity<ResultPaginationDTO> searchAndFilter(
            @Valid @ModelAttribute AccountCriteriaDTO accountCriteriaDTO,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.accountService.getAccountSearch(accountCriteriaDTO, pageable));

    }

}
