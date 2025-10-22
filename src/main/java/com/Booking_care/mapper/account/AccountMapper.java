package com.Booking_care.mapper.account;

import com.Booking_care.domain.Account;
import com.Booking_care.domain.dto.AccountDTO.ResAccountDTO;

/**
 * Mapper for Account entity
 */
public final class AccountMapper {

    private AccountMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Account entity to ResAccountDTO
     * 
     * @param account Account entity
     * @return ResAccountDTO
     */
    public static ResAccountDTO toResAccountDTO(Account account) {
        if (account == null) {
            return null;
        }

        ResAccountDTO res = new ResAccountDTO();
        res.setId(account.getId());
        res.setName(account.getName());
        res.setEmail(account.getEmail());
        res.setPhoneNumber(account.getPhoneNumber());
        res.setAddress(account.getAddress());
        res.setGender(account.getGender());
        res.setBirth(account.getBirth());
        res.setCccd(account.getCccd());
        res.setAvatar(account.getAvatar());
        res.setCreateAt(account.getCreateAt());
        res.setUpdateAt(account.getUpdateAt());

        if (account.getRole() != null) {
            ResAccountDTO.RoleAccount roleAccount = new ResAccountDTO.RoleAccount();
            roleAccount.setId(account.getRole().getId());
            roleAccount.setName(account.getRole().getName());
            res.setRole(roleAccount);
        }

        return res;
    }
}
