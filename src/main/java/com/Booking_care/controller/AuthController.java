package com.Booking_care.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Booking_care.domain.request.ReqLoginDTO;
import com.Booking_care.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountService accountService;

    public AuthController(AccountService accountService,
            AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.accountService = accountService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/auth/login")
    public String login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // tạo authenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUserName(),
                loginDTO.getPassword());

        // xác thực người dùng -> loadUserByUsername
        Authentication auth = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return "ok";
    }

    @GetMapping("/test")
    public String test() {
        return "ok test";
    }
}
