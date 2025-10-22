package com.Booking_care.service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.Booking_care.domain.Account;
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
import com.Booking_care.mapper.account.AccountMapper;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    private final String folder = "booking_care/account/";

    public AccountService(AccountRepository accountRepository, RoleService roleService, PasswordEncoder passwordEncoder,
            CloudinaryService cloudinaryService) {
        this.accountRepository = accountRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }

    public boolean isEmailExits(String email) {
        return this.accountRepository.existsByEmail(email);
    }

    public void validateEmailNotExists(String email) {
        if (this.accountRepository.existsByEmail(email)) {
            throw new IdInvalidException("Email " + email + "đã tồn tại, vui lòng sử dụng email khác.");
        }
    }

    public Account handleCreateAccount(CreateAccountDTO dto) {
        // Validation: check email exists
        if (this.accountRepository.existsByEmail(dto.getEmail())) {
            throw new IdInvalidException(
                    "Email " + dto.getEmail() + " đã tồn tại, Vui lòng sử dụng email khác.");
        }

        try {
            Account acc = new Account();
            acc.setName(dto.getName());
            acc.setEmail(dto.getEmail());
            acc.setPassword(this.passwordEncoder.encode(dto.getPassword()));
            acc.setPhoneNumber(dto.getPhoneNumber());
            acc.setAddress(dto.getAddress());
            acc.setGender(dto.getGender());
            acc.setCccd(dto.getCccd());

            // set role
            Role role = this.roleService.fetchRoleById(dto.getRoleId());
            acc.setRole(role);

            return this.accountRepository.save(acc);
        } catch (Exception e) {
            throw new StorageException("Không thể tạo account: " + e.getMessage(), e);
        }
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
                .map(AccountMapper::toResAccountDTO)
                .collect(Collectors.toList());

        res.setResult(listAcc);
        res.setMeta(meta);

        return res;
    }

    public Account fetchAccountById(long id) {
        Optional<Account> accOptional = this.accountRepository.findById(id);
        if (!accOptional.isPresent()) {
            throw new IdInvalidException("Account với id " + id + " không tồn tại");
        }
        return accOptional.get();
    }

    public Account handleUpdateAccount(UpdateAccountDTO acc) {
        // Validation: check account exists (throws exception if not found)
        Account currentAcc = this.fetchAccountById(acc.getId());

        try {
            currentAcc.setName(acc.getName());
            currentAcc.setPhoneNumber(acc.getPhoneNumber());
            currentAcc.setAddress(acc.getAddress());
            currentAcc.setGender(acc.getGender());
            currentAcc.setCccd(acc.getCccd());
            currentAcc.setBirth(acc.getBirth());

            // set role
            if (acc.getRoleId() != null) {
                Role role = this.roleService.fetchRoleById(acc.getRoleId());
                currentAcc.setRole(role != null ? role : null);
            }

            // upload image
            if (acc.getFile() != null && !acc.getFile().isEmpty()) {
                try {
                    ResCloudinaryDTO resImg = cloudinaryService.uploadToFolder(acc.getFile(), folder,
                            String.valueOf(currentAcc.getId()));
                    currentAcc.setAvatar(resImg.getUrl());
                } catch (Exception e) {
                    throw new StorageException("Không thể upload ảnh: " + e.getMessage(), e);
                }
            }

            return this.accountRepository.save(currentAcc);
        } catch (StorageException e) {
            throw e;
        } catch (Exception e) {
            throw new StorageException("Không thể cập nhật account: " + e.getMessage(), e);
        }
    }

    public void handleDeleteAccount(long id) {
        // Validation: check account exists (throws exception if not found)
        this.fetchAccountById(id);

        try {
            this.accountRepository.deleteById(id);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể xóa account: " + e.getMessage());
        }
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
                .map(AccountMapper::toResAccountDTO)
                .collect(Collectors.toList());

        res.setResult(listAcc);
        res.setMeta(meta);

        return res;
    }

    public Page<Account> getAllAccountWithSpec(AccountCriteriaDTO accountCriteriaDTO, Pageable pageable) {
        Specification<Account> combinedSpec = Specification.where(null);

        if (accountCriteriaDTO.getRoleName() != null && !accountCriteriaDTO.getRoleName().trim().isEmpty()) {
            // Equal:
            combinedSpec = combinedSpec.and(
                    AccountSpecs.roleNameEqual(accountCriteriaDTO.getRoleName().trim()));
        }

        if (accountCriteriaDTO.getGender() != null && !accountCriteriaDTO.getGender().trim().isEmpty()) {
            combinedSpec = combinedSpec.and(
                    AccountSpecs.genderEqual(accountCriteriaDTO.getGender().trim()));
        }

        YearMonth monthYear = accountCriteriaDTO.getMonthYear();
        if (monthYear != null) {
            Instant from = monthYear.atDay(1)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();

            Instant to = monthYear.plusMonths(1).atDay(1)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();

            combinedSpec = combinedSpec.and(
                    AccountSpecs.createdAtBetween(from, to));
        }

        if (accountCriteriaDTO.getCccd() != null && !accountCriteriaDTO.getCccd().trim().isEmpty()) {
            combinedSpec = combinedSpec.and(
                    AccountSpecs.cccdLike(accountCriteriaDTO.getCccd().trim()));
        }

        if (accountCriteriaDTO.getEmail() != null && !accountCriteriaDTO.getEmail().trim().isEmpty()) {
            combinedSpec = combinedSpec.and(
                    AccountSpecs.emailLike(accountCriteriaDTO.getEmail().trim()));
        }

        if (accountCriteriaDTO.getPhoneNumber() != null && !accountCriteriaDTO.getPhoneNumber().trim().isEmpty()) {
            combinedSpec = combinedSpec.and(
                    AccountSpecs.phoneNumberLike(accountCriteriaDTO.getPhoneNumber().trim()));
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

    public void handleLogout(String email) {
        if (email == null || email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // update refresh token = null
        this.updateToken(null, email);
    }

    public Account getAccountByRefreshTokenAndEmail(String token, String email) {
        if (token == null || token.equals("abc")) {
            throw new IdInvalidException("Bạn không có refresh token ở cookie");
        }

        Account account = this.accountRepository.findByRefreshTokenAndEmail(token, email);
        if (account == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        return account;
    }

    public void handleResetPassword(long id, String newPass, String currentPassword) {
        // Validation: check account exists (throws exception if not found)
        Account acc = this.fetchAccountById(id);

        if (!passwordEncoder.matches(currentPassword, acc.getPassword())) {
            throw new IdInvalidException("Mật khẩu hiện tại không đúng");
        }

        try {
            acc.setPassword(passwordEncoder.encode(newPass));
            this.accountRepository.save(acc);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể đặt lại mật khẩu: " + e.getMessage());
        }
    }

    public Account forgotPassword(ResetPasswordRequest reset) {
        Account acc = this.fetchAccountByEmail(reset.getEmail());
        if (acc == null) {
            throw new IdInvalidException("Email không tồn tại");
        }

        try {
            acc.setPassword(passwordEncoder.encode(reset.getPassword()));
            return this.accountRepository.save(acc);
        } catch (Exception e) {
            throw new IdInvalidException("Không thể đặt lại mật khẩu: " + e.getMessage());
        }
    }
}
