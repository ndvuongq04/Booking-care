package com.Booking_care.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;
import com.Booking_care.domain.Account;
import com.Booking_care.domain.request.ReqLoginDTO;
import com.Booking_care.domain.request.accountDTO.CreateAccountDTO;
import com.Booking_care.domain.response.ResAccountDTO;
import com.Booking_care.domain.response.ResLoginDTO;
import com.Booking_care.service.AccountService;
import com.Booking_care.util.SecurityUtil;
import com.Booking_care.util.annotation.ApiMessage;
import com.Booking_care.util.error.IdInvalidException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountService accountService;
    private final SecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${booking-care.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AccountService accountService,
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // tạo authenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUserName(),
                loginDTO.getPassword());

        // xác thực người dùng -> loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();

        Account currentAcc = this.accountService.fetchAccountByEmail(loginDTO.getUserName());

        if (currentAcc != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentAcc.getId(),
                    currentAcc.getName(),
                    currentAcc.getEmail(),
                    currentAcc.getRole().getName().toUpperCase());
            res.setUserLogin(userLogin);
        }

        // create token
        String accessToken = this.securityUtil.createAccessToken(currentAcc.getEmail(), res);
        res.setAccessToken(accessToken);

        // create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(currentAcc.getEmail(), res);

        // update refresh token
        this.accountService.updateToken(refresh_token, currentAcc.getEmail());

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        Account currentAccountDB = this.accountService.fetchAccountByEmail(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();

        if (currentAccountDB != null) {
            userLogin.setId(currentAccountDB.getId());
            userLogin.setEmail(currentAccountDB.getEmail());
            userLogin.setName(currentAccountDB.getName());
            userLogin.setRole(currentAccountDB.getRole().getName().toString().toUpperCase());
        }

        return ResponseEntity.ok().body(userLogin);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get Account by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) throws IdInvalidException {
        if (refresh_token.equals("abc")) {
            throw new IdInvalidException("Bạn không có refresh token ở cookie");
        }
        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // check user by token + email
        Account currentAccount = this.accountService.getAccountByRefreshTokenAndEmail(refresh_token, email);
        if (currentAccount == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        // issue new token/set refresh token as cookies
        ResLoginDTO res = new ResLoginDTO();
        Account currentAccountDB = this.accountService.fetchAccountByEmail(email);
        if (currentAccountDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentAccountDB.getId(),
                    currentAccountDB.getEmail(),
                    currentAccountDB.getName(),
                    currentAccountDB.getRole().getName().toString().toUpperCase());
            res.setUserLogin(userLogin);

        }

        // create access token
        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        // create refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        // update user
        this.accountService.updateToken(refresh_token, currentAccountDB.getEmail());

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration) // sau maxAge -> token hết hạn
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout Account")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // update refresh token = null
        this.accountService.updateToken(null, email);

        // remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new account")
    public ResponseEntity<ResAccountDTO> register(@Valid @RequestBody CreateAccountDTO postManAccount)
            throws IdInvalidException {
        boolean isEmailExist = this.accountService.isEmailExits(postManAccount.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + postManAccount.getEmail() + "đã tồn tại, vui lòng sử dụng email khác.");
        }

        String hashPassword = this.passwordEncoder.encode(postManAccount.getPassword());
        postManAccount.setPassword(hashPassword);
        Account acc = this.accountService.handleCreateAccount(postManAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.accountService.convertToResAccountDTO(acc));
    }
}
