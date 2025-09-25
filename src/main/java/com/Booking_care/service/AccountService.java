package com.Booking_care.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.Otp;
import com.Booking_care.domain.Role;
import com.Booking_care.domain.dto.ResCloudinaryDTO;
import com.Booking_care.domain.dto.AccountDTO.AccountCriteriaDTO;
import com.Booking_care.domain.dto.AccountDTO.CreateAccountDTO;
import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;
import com.Booking_care.domain.dto.AccountDTO.UpdateAccountDTO;
import com.Booking_care.domain.dto.AuthDTO.ResetPasswordRequest;
import com.Booking_care.domain.response.ResultPaginationDTO;
import com.Booking_care.repository.AccountRepository;
import com.Booking_care.service.specification.AccountSpecs;
import com.Booking_care.util.error.IdInvalidException;
import com.Booking_care.util.error.StorageException;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final OtpService otpService;

    private final String folder = "booking_care/account/";

    public AccountService(AccountRepository accountRepository,
            RoleService roleService,
            PasswordEncoder passwordEncoder,
            CloudinaryService cloudinaryService,
            EmailService emailService,
            OtpService otpService) {
        this.accountRepository = accountRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    public boolean isEmailExits(String email) {
        return this.accountRepository.existsByEmail(email);
    }

    public Account handleCreateAccount(CreateAccountDTO dto) {
        Account acc = new Account();
        acc.setName(dto.getName());
        acc.setEmail(dto.getEmail());
        acc.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        acc.setPhoneNumber(dto.getPhoneNumber());
        acc.setAddress(dto.getAddress());
        acc.setGender(dto.getGender());
        acc.setCccd(dto.getCccd());

        // set role default Client (id = 4)
        Role role = this.roleService.fetchRoleById(4);
        acc.setRole(role);

        return this.accountRepository.save(acc);
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
        res.setAvatar(acc.getAvatar());
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

    public Account handleUpdateAccount(UpdateAccountDTO acc) throws StorageException {
        Account currentAcc = this.fetchAccountById(acc.getId());
        if (currentAcc != null) {
            currentAcc.setName(acc.getName());
            currentAcc.setPhoneNumber(acc.getPhoneNumber());
            currentAcc.setAddress(acc.getAddress());
            currentAcc.setGender(acc.getGender());
            currentAcc.setCccd(acc.getCccd());

            // set role
            if (acc.getRoleId() != null) {
                Role role = this.roleService.fetchRoleById(acc.getRoleId());
                currentAcc.setRole(role != null ? role : null);
            }

            // upload image
            if (acc.getFile() != null && !acc.getFile().isEmpty()) {
                ResCloudinaryDTO resImg = cloudinaryService.uploadToFolder(acc.getFile(), folder,
                        String.valueOf(currentAcc.getId()));
                currentAcc.setAvatar(resImg.getUrl());

            }

            currentAcc = this.accountRepository.save(currentAcc);
        }

        return currentAcc; // null
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

    public Account fetchAccountByEmail(String email) {
        return this.accountRepository.findByEmail(email);
    }

    public void updateToken(String token, String email) {
        Account acc = this.fetchAccountByEmail(email);
        if (acc != null) {
            acc.setRefreshToken(token);
            this.accountRepository.save(acc);
        }
    }

    public Account getAccountByRefreshTokenAndEmail(String token, String email) {
        return this.accountRepository.findByRefreshTokenAndEmail(token, email);
    }

    public void handleResetPassword(long id, String newPass) {
        Account acc = this.fetchAccountById(id);
        if (acc != null) {
            acc.setPassword(passwordEncoder.encode(newPass));
            acc = this.accountRepository.save(acc);
        }
    }

    public Account forgotPassword(ResetPasswordRequest reset) throws IdInvalidException {
        Account acc = this.fetchAccountByEmail(reset.getEmail());
        if (acc == null) {
            throw new IdInvalidException("Email không tồn tại");
        }
        acc.setPassword(passwordEncoder.encode(reset.getPassword()));

        return this.accountRepository.save(acc);
    }
}
