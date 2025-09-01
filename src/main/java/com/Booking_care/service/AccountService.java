package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.dto.accountDTO.AccountCriteriaDTO;
import com.Booking_care.domain.dto.accountDTO.CreateAccountDTO;
import com.Booking_care.domain.dto.accountDTO.UpdateAccountDTO;
import com.Booking_care.domain.response.ResAccountDTO;
import com.Booking_care.domain.response.ResCreateAccountDTO;
import com.Booking_care.domain.response.ResUpdateAccountDTO;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.AccountRepository;
import com.Booking_care.service.specification.AccountSpecs;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public boolean isEmailExits(String email) {
        return this.accountRepository.existsByEmail(email);
    }

    public Account handleCreateAccount(CreateAccountDTO dto) {
        // if(acc.getRole() != null){

        // }
        Account acc = new Account();
        acc.setName(dto.getName());
        acc.setEmail(dto.getEmail());
        acc.setPassword(dto.getPassword()); // chưa hash password
        acc.setPhoneNumber(dto.getPhoneNumber());
        acc.setAddress(dto.getAddress());
        acc.setGender(dto.getGender());
        acc.setCccd(dto.getCccd());

        return this.accountRepository.save(acc);
    }

    public ResCreateAccountDTO convertToResCreateAccountDTO(Account acc) {
        ResCreateAccountDTO res = new ResCreateAccountDTO();

        res.setId(acc.getId());
        res.setName(acc.getName());
        res.setPhoneNumber(acc.getPhoneNumber());
        res.setEmail(acc.getEmail());
        res.setGender(acc.getGender());
        res.setAddress(acc.getAddress());
        res.setBirth(acc.getBirth());
        res.setCccd(acc.getCccd());
        res.setCreateAt(acc.getCreateAt());

        return res;
    }

    public ResultPaginationDTO fetchAllAccount(Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Account> page = this.accountRepository.findAll(pageable);

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(page.getTotalPages());
        meta.setTotals(page.getTotalElements());

        // convert
        List<ResAccountDTO> listAcc = page.getContent().stream()
                .map(item -> this.convertToResAccountDTO(item))
                .collect(Collectors.toList());

        res.setResult(listAcc);
        res.setMeta(meta);

        return res;
    }

    public ResAccountDTO convertToResAccountDTO(Account acc) {
        ResAccountDTO res = new ResAccountDTO();
        ResAccountDTO.RoleAccount roleAccount = new ResAccountDTO.RoleAccount();

        if (acc.getRole() != null) {
            roleAccount.setId(acc.getRole().getId());
            roleAccount.setName(acc.getRole().getName());
            res.setRole(roleAccount);
        }

        res.setId(acc.getId());
        res.setName(acc.getName());
        res.setEmail(acc.getEmail());
        res.setPhoneNumber(acc.getPhoneNumber());
        res.setGender(acc.getGender());
        res.setAddress(acc.getAddress());
        res.setBirth(acc.getBirth());
        res.setCccd(acc.getCccd());
        res.setCreateAt(acc.getCreateAt());
        res.setUpdateAt(acc.getUpdateAt());

        return res;
    }

    public Account fetchAccountById(long id) {
        Optional<Account> accOptional = this.accountRepository.findById(id);
        if (accOptional.isPresent()) {
            return accOptional.get();
        }
        return null;
    }

    public Account handleUpdateAccount(UpdateAccountDTO acc) {
        Account currentAcc = this.fetchAccountById(acc.getId());
        if (currentAcc != null) {
            currentAcc.setName(acc.getName());
            currentAcc.setPhoneNumber(acc.getPhoneNumber());
            currentAcc.setAddress(acc.getAddress());
            currentAcc.setGender(acc.getGender());
            currentAcc.setCccd(acc.getCccd());

            // if (acc.getRole() != null) {

            // }

            // update
            currentAcc = this.accountRepository.save(currentAcc);
        }

        return currentAcc; // null
    }

    public ResUpdateAccountDTO convertToResUpdateAccountDTO(Account acc) {
        ResUpdateAccountDTO res = new ResUpdateAccountDTO();
        ResUpdateAccountDTO.RoleAccount roleAccount = new ResUpdateAccountDTO.RoleAccount();

        if (acc.getRole() != null) {
            roleAccount.setId(acc.getRole().getId());
            roleAccount.setName(acc.getRole().getName());
            res.setRoleAccount(roleAccount);
        }

        res.setId(acc.getId());
        res.setName(acc.getName());
        res.setPhoneNumber(acc.getPhoneNumber());
        res.setGender(acc.getGender());
        res.setAddress(acc.getAddress());
        res.setBirth(acc.getBirth());
        res.setCccd(acc.getCccd());
        res.setUpdateAt(acc.getUpdateAt());

        return res;
    }

    public void handleDeleteAccount(long id) {
        this.accountRepository.deleteById(id);
    }

    public ResultPaginationDTO getAccountSearch(AccountCriteriaDTO accountCriteriaDTO, Pageable pageable) {
        Page<Account> listPage = getAllAccountWithSpec(accountCriteriaDTO, pageable);

        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        // từ fe
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        // từ db
        meta.setPages(listPage.getTotalPages());
        meta.setTotals(listPage.getTotalElements());

        // convert
        List<ResAccountDTO> listAcc = listPage.getContent().stream()
                .map(item -> this.convertToResAccountDTO(item))
                .collect(Collectors.toList());

        res.setResult(listAcc);
        res.setMeta(meta);

        return res;
    }

    public Page<Account> getAllAccountWithSpec(AccountCriteriaDTO accountCriteriaDTO, Pageable pageable) {
        Specification<Account> combinedSpec = Specification.where(null);

        if (accountCriteriaDTO.getRole() != null) {
            // roleName -> roleId ( sẽ dùng name query Db lấy roleID )
            String roleName = accountCriteriaDTO.getRole().trim().toUpperCase();
            long roleID = roleName.equals("ADMIN") ? 1
                    : roleName.equals("CLIENT") ? 2
                            : roleName.equals("DOCTOR") ? 3 : 4;

            Specification<Account> currentSpecs = AccountSpecs.roleEqual(roleID);
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (accountCriteriaDTO.getMonthYear() != null) {
            Specification<Account> currentSpecs = AccountSpecs.createAtBetween(accountCriteriaDTO.getMonthYear());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (accountCriteriaDTO.getGender() != null) {
            Specification<Account> currentSpecs = AccountSpecs.genderEqual(accountCriteriaDTO.getGender());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (accountCriteriaDTO.getCccd() != null) {
            Specification<Account> currentSpecs = AccountSpecs.cccdEqual(accountCriteriaDTO.getCccd());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (accountCriteriaDTO.getPhoneNumber() != null) {
            Specification<Account> currentSpecs = AccountSpecs.phoneNumberLike(accountCriteriaDTO.getPhoneNumber());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        if (accountCriteriaDTO.getEmail() != null) {
            Specification<Account> currentSpecs = AccountSpecs.emailLike(accountCriteriaDTO.getEmail());
            combinedSpec = combinedSpec.and(currentSpecs);
        }

        return this.accountRepository.findAll(combinedSpec, pageable);

    }

}
