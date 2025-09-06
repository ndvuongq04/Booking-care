package com.Booking_care.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.Booking_care.domain.Account;
import com.Booking_care.service.AccountService;

@Component("userDetailsService")
public class UserDetailCustom implements UserDetailsService {
    private final AccountService accountService;

    public UserDetailCustom(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account acc = this.accountService.fetchAccountByEmail(username);

        if (acc == null) {
            throw new UsernameNotFoundException("Username/password không hợp lệ");
        }
        return new User(
                acc.getEmail(),
                acc.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + acc.getRole().getName().toUpperCase())));
    }

}
